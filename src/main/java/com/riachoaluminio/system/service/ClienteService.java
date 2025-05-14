package com.riachoaluminio.system.service;

import com.riachoaluminio.system.entity.Cliente;
import com.riachoaluminio.system.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id).map(existingCliente -> {
            existingCliente.setNome(clienteAtualizado.getNome());
            existingCliente.setTelefone(clienteAtualizado.getTelefone());
            existingCliente.setEmail(clienteAtualizado.getEmail());
            existingCliente.setEndereco(clienteAtualizado.getEndereco());
            return clienteRepository.save(existingCliente);
        }).orElseThrow(() -> new RuntimeException("Cliente não encontrado para o id: " + id));
    }

    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }
}
