package com.bancox.BancoX.services;

import com.bancox.BancoX.dtos.ContaRequestDTO;
import com.bancox.BancoX.dtos.ContaResponseDTO;
import com.bancox.BancoX.dtos.ExtratoResponseDto;
import com.bancox.BancoX.entities.Conta;
import com.bancox.BancoX.entities.Transacao;
import com.bancox.BancoX.repositories.ContaRepository;
import com.bancox.BancoX.repositories.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransacaoRepository transacaoRepository;

    public ContaResponseDTO criarConta(ContaRequestDTO conta) {

        Conta novaConta = new Conta();

        Optional<Conta> contaExistente = contaRepository.findByCpf(conta.cpf());

        if (contaExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma conta com este CPF.");
        }
        novaConta.setCpf(conta.cpf());
        novaConta.setNome(conta.nome());
        novaConta.setSaldo(BigDecimal.ZERO);
        novaConta.setSenha(hashSenha(conta.senha()));
        novaConta.setNumeroDaConta(gerarNumeroDaConta());

        Conta contaSalva = contaRepository.save(novaConta);

        return new ContaResponseDTO(
                contaSalva.getId(),
                contaSalva.getNome(),
                contaSalva.getNumeroDaConta(),
                contaSalva.getCpf(),
                contaSalva.getSaldo()
        );
    }

    public Optional<ContaResponseDTO> listarConta(Long id) {

        return contaRepository.findById(id).map(conta -> new ContaResponseDTO(
                conta.getId(),
                conta.getNome(),
                conta.getNumeroDaConta(),
                conta.getCpf(),
                conta.getSaldo()
        ));
    }

    public List<ContaResponseDTO> listarTodas() {
        return contaRepository.findAll().stream().map(
                conta -> new ContaResponseDTO(
                        conta.getId(),
                        conta.getNome(),
                        conta.getNumeroDaConta(),
                        conta.getCpf(),
                        conta.getSaldo()
                )).toList();
    }

    public String gerarNumeroDaConta() {
        Random random = new Random();
        StringBuilder numeroDaConta = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int digito = random.nextInt(10);
            numeroDaConta.append(digito);
        }

        return numeroDaConta.toString();
    }

    private String hashSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    public Conta login(String cpf, String senha) {
        Optional<Conta> contaOpt = contaRepository.findByCpf(cpf);
        if (contaOpt.isPresent()) {
            Conta conta = contaOpt.get();
            if (passwordEncoder.matches(senha, conta.getSenha())) {
                return conta;
            } else {
                throw new IllegalArgumentException("Senha incorreta.");
            }
        } else {
            throw new IllegalArgumentException("Conta não encontrada para o CPF fornecido.");
        }
    }

    public Optional<Conta> findByCpf(String cpf) {
        return contaRepository.findByCpf(cpf);
    }

    public boolean verificarSenha(String senhaDigitada, String senhaArmazenada) {
        return passwordEncoder.matches(senhaDigitada, senhaArmazenada);
    }

    public ContaResponseDTO depositar(Long id, BigDecimal valor) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

        conta.setSaldo(conta.getSaldo().add(valor));
        Conta contaAtualizada = contaRepository.save(conta);

        Transacao transacao = new Transacao();
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setDestino(contaAtualizada);
        transacao.setValor(valor);
        transacaoRepository.save(transacao);

        return new ContaResponseDTO(
                contaAtualizada.getId(),
                contaAtualizada.getNome(),
                contaAtualizada.getNumeroDaConta(),
                contaAtualizada.getCpf(),
                contaAtualizada.getSaldo()
        );
    }

    @Transactional
    public void transferir(Long idOrigem, String cpfDestino, BigDecimal valor) {

        Conta contaOrigem = contaRepository.findById(idOrigem)
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada."));

        Conta contaDestino = contaRepository.findByCpf(cpfDestino)
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada."));

        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para a transferência.");
        }

        if (contaOrigem.getId().equals(contaDestino.getId())) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta.");
        }

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser maior que zero.");
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        Transacao transacao = new Transacao();
        transacao.setOrigem(contaOrigem);
        transacao.setDestino(contaDestino);
        transacao.setValor(valor);
        transacao.setDataTransacao(LocalDateTime.now());
        transacaoRepository.save(transacao);
    }

    public List<ExtratoResponseDto> listarExtrato(Long usuarioId) {

        List<Transacao> transacoes = transacaoRepository.findTransacoesPorUsuario(usuarioId);

        return transacoes.stream().map(transacao -> {

            boolean isDeposito = transacao.getOrigem() == null;
            boolean souOrigem = !isDeposito && transacao.getOrigem().getId().equals(usuarioId);
            boolean isBoleto = transacao.getDestino() == null;
            String outraPessoa;
            String tipo = souOrigem ? "SAIDA" : "ENTRADA";

            if (isDeposito) {
                outraPessoa = "DEPOSITO";
            }
            else if (isBoleto) {
                outraPessoa = "Pagamento de Boleto";
            }
            else if (souOrigem) {
                outraPessoa = transacao.getDestino().getNome();
            }
            else outraPessoa = transacao.getOrigem().getNome();

            return new ExtratoResponseDto(
                    transacao.getId(),
                    transacao.getDataTransacao(),
                    tipo,
                    outraPessoa,
                    transacao.getValor()
            );
        }).toList();
    }

    public ContaResponseDTO pagarBoleto(Long id, BigDecimal valor) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do boleto tem que ser maior que zero");
        }

        if (conta.getSaldo().compareTo(valor) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente!");
        }

        conta.setSaldo(conta.getSaldo().subtract(valor));
        Conta contaAtualizada = contaRepository.save(conta);

        Transacao transacao = new Transacao();
        transacao.setValor(valor);
        transacao.setDataTransacao(LocalDateTime.now());
        transacao.setOrigem(contaAtualizada);
        transacao.setDestino(null);

        transacaoRepository.save(transacao);

        return new ContaResponseDTO(
                contaAtualizada.getId(),
                contaAtualizada.getNome(),
                contaAtualizada.getNumeroDaConta(),
                contaAtualizada.getCpf(),
                contaAtualizada.getSaldo()
        );
    }
}
