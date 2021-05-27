package com.carlos.curso_mc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carlos.curso_mc.domain.Pedido;
import com.carlos.curso_mc.repositories.PedidoRepository;
import com.carlos.curso_mc.services.excptions.ObjectNotFoundException;


@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repo;

	public Pedido find(Integer id) {		
		Optional<Pedido> obj = repo.findById(id);
		
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! Id: " + id +
				", Tipo:" + Pedido.class.getName()));
	}
}
