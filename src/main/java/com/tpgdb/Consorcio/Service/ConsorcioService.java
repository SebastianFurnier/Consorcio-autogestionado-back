package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioRequestDto;
import com.tpgdb.Consorcio.Dto.Consorcio.ConsorcioResponseDto;
import com.tpgdb.Consorcio.Dto.Consorcio.UnirseConsorcioRequestDto;
import com.tpgdb.Consorcio.Exception.InvalidConsorcioException;
import com.tpgdb.Consorcio.Exception.MaxPartnerLimitExceededException;
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

                // Crear la entidad Consorcio
                Consorcio consorcio = new Consorcio(
                                requestDto.getNombre(),
                                codigo,
                                user,
                                requestDto.getMaxPartners());

                Consorcio savedConsorcio = consortioRepository.save(consorcio);

                // Crear Partner para el creador (ADMIN)
                Partner partner = new Partner(user, savedConsorcio, Partner.PartnerRole.ADMIN);
                partnerRepository.save(partner);
                // Recalculamos para que el primer socio (ADMIN) tenga el 100% inmediatamente
                recalcularParticipaciones(savedConsorcio.getId());

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

                // Buscar consorcio (buscarlo aunque esté inactivo para poder reactivarlo)
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
                                long miembrosActuales = partnerRepository
                                                .findByConsorcioIdAndActiveIsTrue(consorcio.getId()).size();
                                if (miembrosActuales >= consorcio.getMaxPartners()) {
                                        throw new MaxPartnerLimitExceededException(
                                                        "Se alcanzó la cantidad máxima de partners permitidos para este consorcio");
                                }

                                partner.setActive(true);
                                partnerRepository.save(partner);

                                // Si el consorcio estaba inactivo, revive al entrar el primero.
                                if (!consorcio.isActive()) {
                                        consorcio.setActive(true);
                                        consortioRepository.save(consorcio);
                                }

                                // Recalcular participaciones de todos los socios activos
                                recalcularParticipaciones(consorcio.getId());

                                return buildConsorcioResponse(consorcio, partner.getRole(), miembrosActuales + 1);
                        }
                }

                long cantidadMiembrosActual = partnerRepository.findByConsorcioIdAndActiveIsTrue(consorcio.getId())
                                .size();

                if (cantidadMiembrosActual >= consorcio.getMaxPartners()) {
                        throw new MaxPartnerLimitExceededException(
                                        "Se alcanzó la cantidad máxima de partners permitidos para este consorcio");
                }

                if (!consorcio.isActive()) {
                        consorcio.setActive(true);
                        consortioRepository.save(consorcio);
                }

                Partner nuevoPartner = new Partner(user, consorcio, Partner.PartnerRole.MEMBER);
                partnerRepository.save(nuevoPartner);

                // Recalcular participaciones de todos los socios activos
                recalcularParticipaciones(consorcio.getId());

                return buildConsorcioResponse(consorcio, Partner.PartnerRole.MEMBER, cantidadMiembrosActual + 1);
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

        public ConsorcioResponseDto actualizarNombreConsorcio(Long consorcioId, String nuevoNombre, Long userId) {
                // Obtener consorcio
                Consorcio consorcio = consortioRepository.findById(consorcioId)
                                .orElseThrow(() -> new InvalidConsorcioException("Consorcio no encontrado"));

                if (!consorcio.isActive()) {
                        throw new InvalidConsorcioException("Este consorcio ya no está activo.");
                }

                // Obtener usuario
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new InvalidConsorcioException("Usuario no encontrado"));

                // Verificar que el usuario sea ADMIN
                Partner partner = partnerRepository.findByUserAndConsorcio(user, consorcio)
                                .orElseThrow(() -> new InvalidConsorcioException("No tienes acceso a este consorcio"));

                if (!partner.isActive()) {
                        throw new InvalidConsorcioException("Ya no eres miembro de este consorcio.");
                }

                if (partner.getRole() != Partner.PartnerRole.ADMIN) {
                        throw new InvalidConsorcioException(
                                        "Solo los administradores pueden editar el nombre del consorcio");
                }

                // Validar que el nombre no esté vacío o solo espacios
                if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
                        throw new InvalidConsorcioException(
                                        "El nombre del consorcio no puede estar vacío o contener solo espacios");
                }

                // Actualizar nombre
                consorcio.setNombre(nuevoNombre.trim());
                Consorcio consorcioActualizado = consortioRepository.save(consorcio);

                long cantidadMiembros = partnerRepository
                                .findByConsorcioIdAndActiveIsTrue(consorcioId)
                                .size();

                return buildConsorcioResponse(consorcioActualizado, partner.getRole(), cantidadMiembros);
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
                                cantidadMiembros,
                                consorcio.getMaxPartners());
        }

        /**
         * Recalcula y actualiza las participaciones de todos los socios activos del
         * consorcio
         * para que sean equitativas (100 / cantidad de socios activos)
         */
        void recalcularParticipaciones(Long consorcioId) {
                List<Partner> sociosActivos = partnerRepository.findByConsorcioIdAndActiveIsTrue(consorcioId);

                if (sociosActivos.isEmpty()) {
                        return;
                }

                float participacionEquitativa = 100.0f / sociosActivos.size();

                for (Partner socio : sociosActivos) {
                        socio.setParticipation(participacionEquitativa);
                        partnerRepository.save(socio);
                }
        }
}