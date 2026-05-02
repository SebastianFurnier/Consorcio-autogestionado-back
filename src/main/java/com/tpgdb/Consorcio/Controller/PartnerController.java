package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.partner.PartnerEditRequestDTO;
import com.tpgdb.Consorcio.Dto.partner.PartnerRequestDto;
import com.tpgdb.Consorcio.Dto.partner.PartnerResponseDto;
import com.tpgdb.Consorcio.Service.PartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partner")
public class PartnerController {

    private final PartnerService service;

    @PostMapping("/save")
    public ResponseEntity<?> createPartner(@Valid @RequestBody PartnerRequestDto partnerDto) {
        service.createNewPartner(partnerDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePartner(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        service.deletePartnerById(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene los socios filtrados por consorcio.
     * * @param consorcioId ID que viene del frontend como ?consorcioId=...
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, List<PartnerResponseDto>>> getAll(@RequestParam Long consorcioId) {
        List<PartnerResponseDto> partnerList = service.getAllActivePartnersByConsorcio(consorcioId);
        return ResponseEntity.ok(Map.of("response", partnerList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerResponseDto> getPartner(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPartnerById(id));
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editPartner(@Valid @RequestBody PartnerEditRequestDTO partnerDto) {
        service.editPartner(partnerDto);

        return ResponseEntity.ok().build();
    }
}