package com.tpgdb.Consorcio.Controller;

import com.tpgdb.Consorcio.Dto.Debt.DebtResponseDto;
import com.tpgdb.Consorcio.Service.DebtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/debt")
@AllArgsConstructor
public class DebtController {
    private final DebtService debtService;

    @GetMapping("/all")
    public ResponseEntity<List<DebtResponseDto>> getAllDebtsFromPartnerInRepository(
            @RequestParam Long consorcioId, @RequestParam Long partnerId) {
        List<DebtResponseDto> debtList = debtService.getDebtFromPartnerAndConsorcio(consorcioId, partnerId);

        return ResponseEntity.ok(debtList);
    }
}
