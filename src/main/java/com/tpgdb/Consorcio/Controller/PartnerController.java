package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.PartnerRequestDto;
//import com.tpgdb.Consorcio.Model.Partner;
import com.tpgdb.Consorcio.Service.PartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partner")
@CrossOrigin("*")
public class PartnerController {
    final private PartnerService service;

    @PostMapping("/save")
    public ResponseEntity<?> createPartner(@Valid @RequestBody PartnerRequestDto partnerDto) {
        service.createNewPartner(partnerDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePartner(@PathVariable Long id) {
        service.deletePartnerById(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, List<PartnerRequestDto>>> getAll() {
        List<PartnerRequestDto> partnerList = service.getAllActivePartners();

        return ResponseEntity.ok(Map.of("response", partnerList));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editPartner(@Valid @RequestBody PartnerRequestDto partnerDto) {
        service.editPartner(partnerDto);

        return ResponseEntity.ok().build();
    }
}
