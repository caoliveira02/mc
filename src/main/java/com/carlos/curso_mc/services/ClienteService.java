package com.carlos.curso_mc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carlos.curso_mc.domain.Cidade;
import com.carlos.curso_mc.domain.Cliente;
import com.carlos.curso_mc.domain.Endereco;
import com.carlos.curso_mc.domain.enums.TipoCliente;
import com.carlos.curso_mc.dto.ClienteDTO;
import com.carlos.curso_mc.dto.ClienteNewDTO;
import com.carlos.curso_mc.repositories.ClienteRepository;
import com.carlos.curso_mc.repositories.EnderecoRepository;
import com.carlos.curso_mc.services.excptions.DataIntegrityException;
import com.carlos.curso_mc.services.excptions.ObjectNotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;

	public Cliente find(Integer id) {		
		Optional<Cliente> obj = repo.findById(id);
		
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id +
				", Tipo:" + Cliente.class.getName()));
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
		//return repo.save(obj);
	}
	
	public Cliente update(Cliente obj) {
		Cliente objNew = find(obj.getId());
		updateData(objNew, obj);
		return repo.save(objNew);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas");
		}
	}
	
	public List<Cliente> findAll() {
		return repo.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}
		
	public Cliente fromDTO(ClienteNewDTO objDto) {
		
		Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCofOuCnpj(), TipoCliente.toEnum(objDto.getTipo()));
		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		
		if(objDto.getTelefone2() != null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}
		
		if(objDto.getTelefone3() != null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}
		
		return cli;
	}
	
	private void updateData(Cliente objNew, Cliente obj) {
		objNew.setNome(obj.getNome());
		objNew.setEmail(obj.getEmail());		
	}
}
