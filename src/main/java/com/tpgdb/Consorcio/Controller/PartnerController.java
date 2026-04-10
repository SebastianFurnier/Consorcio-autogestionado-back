package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.PartnerRequestDto;
import com.tpgdb.Consorcio.Service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partner")
@CrossOrigin("*")
public class PartnerController {
    final private PartnerService service;

    @PostMapping("/save")
    public void createPartner(@RequestBody PartnerRequestDto partnerDto) {
        service.createNewPartner(partnerDto);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePartner(@PathVariable Long id) {
        service.deletePartnerById(id);
    }

    @GetMapping("/all")
    public List<PartnerRequestDto> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/deleteAll")
    public void deleteAllPartners() {
        service.deleteAll();
    }
}
