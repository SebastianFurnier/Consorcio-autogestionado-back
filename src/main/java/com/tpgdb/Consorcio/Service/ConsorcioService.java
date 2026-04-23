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
import java.util.Optional;

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
                // Obtener usuario
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new InvalidConsorcioException("Usuario no encontrado"));

                // Normalizar código (quitar guiones y espacios, pasar a Mayúsculas)
                String codigoLimpio = requestDto.getCodigoInvitacion().trim().replace("-", "").toUpperCase();

                // Validar longitud mínima para evitar errores de substring en formatCode
                if (codigoLimpio.length() != 6) {
                        throw new InvalidConsorcioException("El código debe tener 6 caracteres");
                }

                String codigoFormateado = formatCode(codigoLimpio);

                // Buscar consorcio (buscarlo aunque esté inactivo para poder
                // reactivarlo)
                Consorcio consorcio = consortioRepository.findByCodigoInvitacion(codigoFormateado)
                                .orElseThrow(() -> new InvalidConsorcioException(
                                                "Código de invitación inválido o consorcio inexistente"));

                // Verificar si existe el vínculo (Partner) previo
                Optional<Partner> existingPartner = partnerRepository.findByUserAndConsorcio(user, consorcio);

                if (existingPartner.isPresent()) {
                        Partner partner = existingPartner.get();

                        if (partner.isActive()) {
                                throw new InvalidConsorcioException("Ya perteneces a este consorcio");
                        } else {
                                partner.setActive(true);
                                partnerRepository.save(partner);

                                // Si el consorcio estaba inactivo (porque se habían ido todos), revive al
                                // entrar el primero.
                                if (!consorcio.isActive()) {
                                        consorcio.setActive(true);
                                        consortioRepository.save(consorcio);
                                }

                                long cantidadMiembros = partnerRepository
                                                .findByConsorcioIdAndActiveIsTrue(consorcio.getId()).size();
                                return buildConsorcioResponse(consorcio, partner.getRole(), cantidadMiembros);
                        }
                }

                // Si no existía el vínculo, crear uno nuevo
                if (!consorcio.isActive()) {
                        consorcio.setActive(true);
                        consortioRepository.save(consorcio);
                }

                Partner nuevoPartner = new Partner(user, consorcio, Partner.PartnerRole.MEMBER);
                partnerRepository.save(nuevoPartner);

                long cantidadMiembros = partnerRepository.findByConsorcioIdAndActiveIsTrue(consorcio.getId()).size();
                return buildConsorcioResponse(consorcio, Partner.PartnerRole.MEMBER, cantidadMiembros);
        }

        public List<ConsorcioResponseDto> obtenerMisConsorcios(Long userId) {
                // Obtenemos los vínculos activos
                List<Partner> partners = partnerRepository.findByUserIdAndActiveIsTrue(userId);

                return partners.stream()
                                .filter(partner -> partner.getConsorcio().isActive())
                                .map(partner -> {
                                        long cantidadMiembros = partnerRepository
                                                        .findByConsorcioIdAndActiveIsTrue(
                                                                        partner.getConsorcio().getId())
                                                        .size();
                                        return buildConsorcioResponse(partner.getConsorcio(), partner.getRole(),
                                                        cantidadMiembros);
                                })
                                .collect(Collectors.toList());
        }

        public ConsorcioResponseDto obtenerConsorcioById(Long consorcioId, Long userId) {
                Consorcio consorcio = consortioRepository.findById(consorcioId)
                                .orElseThrow(() -> new InvalidConsorcioException("Consorcio no encontrado"));

                if (!consorcio.isActive()) {
                        throw new InvalidConsorcioException("Este consorcio ya no está activo.");
                }

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new InvalidConsorcioException("Usuario no encontrado"));
                Partner partner = partnerRepository.findByUserAndConsorcio(user, consorcio)
                                .orElseThrow(() -> new InvalidConsorcioException("No tienes acceso a este consorcio"));
                if (!partner.isActive()) {
                        throw new InvalidConsorcioException("Ya no eres miembro de este consorcio.");
                }

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
