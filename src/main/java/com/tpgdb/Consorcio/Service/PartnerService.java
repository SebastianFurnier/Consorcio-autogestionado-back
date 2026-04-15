package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.PartnerRequestDto;
import com.tpgdb.Consorcio.Exception.InvalidDataPartnerException;
import com.tpgdb.Consorcio.Exception.InvalidPartnerIDException;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {
    final private PartnerRepository repository;

    private void validateApartmenInUse(PartnerRequestDto partnerDto) {
        if (repository.existsPartnerByApartmentAndActiveIsTrue(partnerDto.getApartment())) {
            throw new InvalidDataPartnerException("El departamento esta asociado a otro socio.");
        }
    }

    public void createNewPartner(PartnerRequestDto partnerDto) {

        validateApartmenInUse(partnerDto);

        Partner partner = new Partner(
                partnerDto.getName(),
                partnerDto.getApartment(),
                partnerDto.getParticipation(),
                partnerDto.getEmail(),
                partnerDto.getPhone()
        );

        repository.save(partner);
    }

    public void deletePartnerById(Long id) {

        Partner partner = repository.findById(id).orElseThrow(() ->
                new InvalidPartnerIDException("El id no esta asociado a ningun socio")
        );

        partner.setActive(false);

        repository.save(partner);
    }

    public List<PartnerRequestDto> getAllActivePartners() {
        List<Partner> partnerList = repository.findAllByActiveIsTrue();

        return partnerList.stream()
                .map(partner -> new PartnerRequestDto(
                        partner.getId(),
                        partner.getName(),
                        partner.getApartment(),
                        partner.getParticipation(),
                        partner.getEmail(),
                        partner.getPhone()
                )).toList();
    }

    public void editPartner(PartnerRequestDto partnerDto) {

        validateApartmenInUse(partnerDto);

        Partner partner = repository.findById(partnerDto.getId()).orElseThrow(() ->
                new InvalidPartnerIDException("El id no esta asociado a ningun socio")
        );

        partner.setName(partnerDto.getName());
        partner.setApartment(partnerDto.getApartment());
        partner.setPhone(partnerDto.getPhone());
        partner.setEmail(partnerDto.getEmail());
        partner.setParticipation(partnerDto.getParticipation());

        repository.save(partner);
    }
}
