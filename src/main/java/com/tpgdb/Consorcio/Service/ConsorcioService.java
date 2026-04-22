package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioRequestDto;
import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioResponseDto;
import com.tpgdb.Consorcio.Dto.Consorcio.UnirseConsorcioRequestDto;
import com.tpgdb.Consorcio.Exception.InvalidConsorcioException;
import com.tpgdb.Consorcio.Model.Consorcio;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Model.User;
import com.tpgdb.Consorcio.Repository.ConsorcioRepository;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import com.tpgdb.Consorcio.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsorcioService {

    private final ConsorcioRepository consortioRepository;
    private final PartnerRepository partnerRepository;
    private final UserRepository userRepository;

    /**
     * Generar código de invitación único de 6 caracteres alfanuméricos
     * Formato: ABC-123
     */
    private String generateInvitationCode() {
        String code;
        do {
            // Generar código aleatorio de 6 caracteres (mayúsculas + números)
            code = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 6)
                    .toUpperCase();
        } while (consortioRepository.existsByCodigoInvitacion(code));

        return formatCode(code);
    }

    private String formatCode(String code) {
        return code.substring(0, 3) + "-" + code.substring(3, 6);
    }

    public ConsorcioResponseDto crearConsorcio(ConsorcioRequestDto requestDto, Long userId) {
        // Obtener usuario autenticado
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidConsorcioException("Usuario no encontrado"));

        // Generar código de invitación
        String codigo = generateInvitationCode();

        Consorcio consorcio = new Consorcio(
                requestDto.getNombre(),
                codigo,
                user);

        Consorcio savedConsorcio = consortioRepository.save(consorcio);

        // Crear Partner para el creador (ADMIN)
        Partner partner = new Partner(user, savedConsorcio, Partner.PartnerRole.ADMIN);
        partnerRepository.save(partner);

        return buildConsorcioResponse(savedConsorcio, Partner.PartnerRole.ADMIN, 1);
    }

    public ConsorcioResponseDto unirseConsorcio(UnirseConsorcioRequestDto requestDto, Long userId) {
        // Obtener usuario autenticado
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidConsorcioException("Usuario no encontrado"));

        // Limpiar y formatear el código recibido para buscar
        String codigoFormateado = formatCode(requestDto.getCodigoInvitacion().replace("-", "").toUpperCase());

        // Buscar consorcio por código de invitación formateado
        Consorcio consorcio = consortioRepository.findByCodigoInvitacion(codigoFormateado)
                .orElseThrow(() -> new InvalidConsorcioException("Código de invitación inválido"));

        // Validar que el usuario no pertenezca ya al consorcio
        if (partnerRepository.existsByUserAndConsorcio(user, consorcio)) {
            throw new InvalidConsorcioException("Ya perteneces a este consorcio");
        }

        // Crear Partner para el nuevo miembro (MEMBER)
        Partner partner = new Partner(user, consorcio, Partner.PartnerRole.MEMBER);
        partnerRepository.save(partner);

        long cantidadMiembros = partnerRepository.findByConsorcioIdAndActiveIsTrue(consorcio.getId()).size();

        return buildConsorcioResponse(consorcio, Partner.PartnerRole.MEMBER, cantidadMiembros);
    }

    public List<ConsorcioResponseDto> obtenerMisConsorcios(Long userId) {
        // Obtener todos los partners del usuario
        List<Partner> partners = partnerRepository.findByUserIdAndActiveIsTrue(userId);

        // Mapear a DTOs
        return partners.stream()
                .map(partner -> {
                    long cantidadMiembros = partnerRepository
                            .findByConsorcioIdAndActiveIsTrue(partner.getConsorcio().getId())
                            .size();
                    return buildConsorcioResponse(partner.getConsorcio(), partner.getRole(), cantidadMiembros);
                })
                .collect(Collectors.toList());
    }

    public ConsorcioResponseDto obtenerConsorcioById(Long consorcioId, Long userId) {
        // Obtener consorcio
        Consorcio consorcio = consortioRepository.findById(consorcioId)
                .orElseThrow(() -> new InvalidConsorcioException("Consorcio no encontrado"));

        // Obtener usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidConsorcioException("Usuario no encontrado"));

        // Verificar que el usuario pertenece al consorcio
        Partner partner = partnerRepository.findByUserAndConsorcio(user, consorcio)
                .orElseThrow(() -> new InvalidConsorcioException("No tienes acceso a este consorcio"));

        // Obtener cantidad de miembros activos
        long cantidadMiembros = partnerRepository
                .findByConsorcioIdAndActiveIsTrue(consorcioId)
                .size();

        return buildConsorcioResponse(consorcio, partner.getRole(), cantidadMiembros);
    }

    private ConsorcioResponseDto buildConsorcioResponse(Consorcio consorcio, Partner.PartnerRole role,
            long cantidadMiembros) {
        return new ConsorcioResponseDto(
                consorcio.getId(),
                consorcio.getNombre(),
                consorcio.getCodigoInvitacion(),
                consorcio.getCreadoPor().getNombre(),
                consorcio.getFechaCreacion(),
                role.toString(),
                cantidadMiembros);
    }
}
