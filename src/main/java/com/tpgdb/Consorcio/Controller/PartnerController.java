package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.PartnerRequestDto;
import com.tpgdb.Consorcio.Service.PartnerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partner")
@CrossOrigin("*")
public class PartnerController {
    final private PartnerService service;

    @PostMapping("/save")
    public void createPartner(@Valid @RequestBody PartnerRequestDto partnerDto) {
        service.createNewPartner(partnerDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable Long id) {
        if (!service.deletePartnerById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(null);
    }

    @GetMapping("/all")
    public List<PartnerRequestDto> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/deleteAll")
    public void deleteAllPartners() {
        service.deleteAll();
    }

    @PutMapping("/edit")
    public void editPartner(@Valid @RequestBody PartnerRequestDto partnerDto) {
        service.editPartner(partnerDto);
    }
}
