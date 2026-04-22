package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.PartnerRequestDto;
import com.tpgdb.Consorcio.Dto.PartnerResponseDto;
import com.tpgdb.Consorcio.Dto.PartnerResponseDto;
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

    public void deletePartnerById(Long id) {
        Partner partner = repository.findById(id)
                .orElseThrow(() -> new InvalidPartnerIDException("El id no esta asociado a ningun socio"));

        partner.setActive(false);
        repository.save(partner);
    }

    public List<PartnerResponseDto> getAllActivePartners() {
        return repository.findAllByActiveIsTrue()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public void editPartner(PartnerRequestDto partnerDto) {
        Partner partner = repository.findById(partnerDto.getId())
                .orElseThrow(() -> new InvalidPartnerIDException("El id no esta asociado a ningun socio"));

        if (partnerDto.getApartment() != null) {
            partner.setApartment(partnerDto.getApartment());
        }

        if (partnerDto.getParticipation() > 0) {
            partner.setParticipation(partnerDto.getParticipation());
        }

        if (partnerDto.getRole() != null) {
            partner.setRole(
                    Partner.PartnerRole.valueOf(partnerDto.getRole().toUpperCase()));
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