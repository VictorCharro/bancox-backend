package com.bancox.BancoX.controller;

import com.bancox.BancoX.dtos.ContaRequestDTO;
import com.bancox.BancoX.dtos.ContaResponseDTO;
import com.bancox.BancoX.dtos.ExtratoResponseDto;
import com.bancox.BancoX.dtos.LoginRequestDTO;
import com.bancox.BancoX.dtos.TransferenciaRequestDTO;
import com.bancox.BancoX.entities.Conta;
import com.bancox.BancoX.security.TokenService;
import com.bancox.BancoX.services.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/conta")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<ContaResponseDTO> criarConta(@RequestBody ContaRequestDTO conta) {
        ContaResponseDTO novaConta = contaService.criarConta(conta);
        return ResponseEntity.status(201).body(novaConta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> listarConta(@PathVariable Long id) {
        Optional<ContaResponseDTO> conta = contaService.listarConta(id);
        return conta
                .map(value -> ResponseEntity.ok().body(value))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ContaResponseDTO> listarTodas() {
        return contaService.listarTodas();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO conta) {
        Conta contaLogada = contaService.login(conta.cpf(), conta.senha());

        String token = tokenService.generateToken(contaLogada);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
                "id", contaLogada.getId(),
                "nome", contaLogada.getNome(),
                "numeroConta", contaLogada.getNumeroDaConta()));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/depositar/{id}")
    public ResponseEntity<ContaResponseDTO> depositar(@PathVariable Long id, @RequestBody Map<String, BigDecimal> valorMap) {
        return ResponseEntity.ok(contaService.depositar(id, valorMap.get("valor")));
    }

    @PostMapping("/transferir/{idOrigem}")
    public ResponseEntity<String> transferir(@PathVariable Long idOrigem, @RequestBody TransferenciaRequestDTO dto) {
        contaService.transferir(idOrigem, dto.cpfDestino(), dto.valor());
        return ResponseEntity.ok("Transferência realizada com sucesso.");
    }

    @GetMapping("/extrato/{id}")
    public ResponseEntity<List<ExtratoResponseDto>> listarExtrato(@PathVariable Long id){
        return ResponseEntity.ok(contaService.listarExtrato(id));
    }

    @PostMapping("/pagar-boleto/{id}")
    public ResponseEntity<ContaResponseDTO> pagarBoleto(@PathVariable Long id, @RequestBody Map<String, BigDecimal> valorMap) {
        return ResponseEntity.ok(contaService.pagarBoleto(id, valorMap.get("valor")));
    }
}
