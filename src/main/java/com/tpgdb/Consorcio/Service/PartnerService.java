package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.partner.PartnerEditRequestDTO;
import com.tpgdb.Consorcio.Dto.partner.PartnerRequestDto;
import com.tpgdb.Consorcio.Dto.partner.PartnerResponseDto;
import com.tpgdb.Consorcio.Exception.InvalidDataPartnerException;
import com.tpgdb.Consorcio.Exception.InvalidPartnerIDException;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Model.User;
import com.tpgdb.Consorcio.Model.Consorcio;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import com.tpgdb.Consorcio.Repository.UserRepository;
import com.tpgdb.Consorcio.Repository.ConsorcioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository repository;
    private final UserRepository userRepository;
    private final ConsorcioRepository consorcioRepository;

    public void createNewPartner(PartnerRequestDto partnerDto) {
        User user = userRepository.findById(partnerDto.getUserId())
                .orElseThrow(() -> new InvalidDataPartnerException("El usuario no existe"));

        Consorcio consorcio = consorcioRepository.findById(partnerDto.getConsorcioId())
                .orElseThrow(() -> new InvalidDataPartnerException("El consorcio no existe"));

        if (repository.existsByUserAndConsorcio(user, consorcio)) {
            throw new InvalidDataPartnerException("El usuario ya pertenece a este consorcio");
        }

        Partner.PartnerRole role = Partner.PartnerRole.valueOf(
                partnerDto.getRole().toUpperCase());

        Partner partner = new Partner(user, consorcio, role);

        if (partnerDto.getApartment() != null) {
            partner.setApartment(partnerDto.getApartment());
        }

        if (partnerDto.getParticipation() > 0) {
            partner.setParticipation(partnerDto.getParticipation());
        }

        repository.save(partner);
    }

    public void deletePartnerById(Long id, Long authenticatedUserId) {
        Partner partnerToDelete = repository.findById(id)
                .orElseThrow(() -> new InvalidPartnerIDException("El id no esta asociado a ningun socio"));

        Long consorcioId = partnerToDelete.getConsorcio().getId();

        // Buscamos al que está pidiendo borrar
        Partner authenticatedPartner = repository
                .findByUserIdAndConsorcioIdAndActiveIsTrue(authenticatedUserId, consorcioId)
                .orElseThrow(() -> new InvalidDataPartnerException("No perteneces a este consorcio"));

        // Lógica de Permisos:
        // Se puede borrar si: Es ADMIN
        boolean isAdmin = authenticatedPartner.getRole() == Partner.PartnerRole.ADMIN;
        boolean isSelfDelete = partnerToDelete.getUser().getId().equals(authenticatedUserId);

        if (!isAdmin && !isSelfDelete) {
            throw new InvalidDataPartnerException("No tienes permisos para eliminar a este socio");
        }

        // Soft Delete del socio
        partnerToDelete.setActive(false);
        repository.save(partnerToDelete);

        // Verificamos si quedaron socios activos
        List<Partner> remainingActivePartners = repository.findByConsorcioIdAndActiveIsTrue(consorcioId);

        // Si no queda nadie, desactivamos el consorcio (Cierre de grupo)
        if (remainingActivePartners.isEmpty()) {
            Consorcio consorcio = partnerToDelete.getConsorcio();
            consorcio.setActive(false);
            consorcioRepository.save(consorcio);
        }
    }

    public List<PartnerResponseDto> getAllActivePartnersByConsorcio(Long consorcioId) {
        return repository.findByConsorcioIdAndActiveIsTrue(consorcioId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public void editPartner(PartnerEditRequestDTO partnerDto) {
        Partner partner = repository.findById(partnerDto.getId())
                .orElseThrow(() -> new InvalidPartnerIDException("El id no esta asociado a ningun socio"));

        Consorcio consorcio = partner.getConsorcio();

        partner.setApartment(partnerDto.getApartment());
        partner.setParticipation(partnerDto.getParticipation());

        if (partnerDto.getRole() != null) {

            if (partner.getRole() == Partner.PartnerRole.ADMIN) {
                int min_admin_allowed = 1;
                if (repository.countPartnerByRoleAndConsorcio_Id(
                        Partner.PartnerRole.ADMIN, consorcio.getId()) == min_admin_allowed){
                    throw new InvalidDataPartnerException("Debe haber al menos un admin en un consorcio");
                };
                partner.setRole(
                        Partner.PartnerRole.valueOf(partnerDto.getRole().toUpperCase()));
            }
        }

        repository.save(partner);
    }

    private PartnerResponseDto convertToDto(Partner partner) {
        return new PartnerResponseDto(
                partner.getId(),
                partner.getUser().getId(),
                partner.getConsorcio().getId(),
                partner.getUser().getNombre(),
                partner.getUser().getEmail(),
                partner.getApartment(),
                partner.getParticipation(),
                partner.getRole().toString());
    }
}