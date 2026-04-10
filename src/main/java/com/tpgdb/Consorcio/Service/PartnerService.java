package com.tpgdb.Consorcio.Service;

import com.tpgdb.Consorcio.Dto.PartnerRequestDto;
import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerService {
    final private PartnerRepository repository;

    public void createNewPartner(PartnerRequestDto partnerDto) {
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
        repository.deleteById(id);
    }

    public List<PartnerRequestDto> getAll() {
        List<Partner> partnerList = repository.findAll();

        return partnerList.stream()
                .map(partner -> {
                    return new PartnerRequestDto(
                            partner.getId(),
                            partner.getName(),
                            partner.getApartment(),
                            partner.getParticipation(),
                            partner.getEmail(),
                            partner.getPhone()
                    );
                }).toList();

    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void editPartner(PartnerRequestDto partnerDto) {
        Partner partner = repository.findById(partnerDto.getId()).orElse(null);
        partner.setName(partnerDto.getName());
        partner.setApartment(partnerDto.getApartment());
        partner.setPhone(partnerDto.getPhone());
        partner.setEmail(partnerDto.getEmail());
        partner.setParticipation(partnerDto.getParticipation());

        repository.save(partner);
    }
}
