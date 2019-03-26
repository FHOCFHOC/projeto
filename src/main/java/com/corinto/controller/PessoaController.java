package com.corinto.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.corinto.model.Pessoa;
import com.corinto.model.Telefone;
import com.corinto.repository.PessoaRepository;
import com.corinto.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;

	@RequestMapping(method=RequestMethod.GET, value="/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
		mv.addObject("pessoaobj", new Pessoa());
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		mv.addObject("pessoas", pessoaIt);
		return mv;
	}
	
	@RequestMapping(method=RequestMethod.POST, value="**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
			mv.addObject("pessoas", pessoaIt);
			mv.addObject("pessoaobj", pessoa);
			
			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage());
			}
			
			mv.addObject("msg", msg);
			return mv;
		}
		
		pessoaRepository.save(pessoa);
		
		ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		mv.addObject("pessoas", pessoaIt);
		mv.addObject("pessoaobj", new Pessoa());
		return mv;
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoaIt = pessoaRepository.findAll();
		mv.addObject("pessoas", pessoaIt);
		mv.addObject("pessoaobj", new Pessoa());
		return mv;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");		
		mv.addObject("pessoaobj", pessoa.get());
		return mv;
	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {		
		pessoaRepository.deleteById(idpessoa);
		ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");		
		mv.addObject("pessoas", pessoaRepository.findAll());
		mv.addObject("pessoaobj", new Pessoa());
		return mv;
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa) {		
		ModelAndView mv = new ModelAndView("cadastro/cadastropessoa");		
		mv.addObject("pessoas", pessoaRepository.findPessoaByName(nomepesquisa));
		mv.addObject("pessoaobj", new Pessoa());
		return mv;
	}
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView mv = new ModelAndView("cadastro/telefones");		
		mv.addObject("pessoaobj", pessoa.get());
		mv.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return mv;
	}
	
	@PostMapping("**/addfonePessoa/{pessoaid}")
	public ModelAndView addfonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {		
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		telefone.setPessoa(pessoa);
		
		telefoneRepository.save(telefone);
		
		ModelAndView mv = new ModelAndView("cadastro/telefones");
		mv.addObject("pessoaobj", pessoa);
		mv.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		return mv;
	}
	
	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView removertelefone(@PathVariable("idtelefone") Long idtelefone) {		
		
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		telefoneRepository.deleteById(idtelefone);
		ModelAndView mv = new ModelAndView("cadastro/telefones");
		mv.addObject("pessoaobj", pessoa);
		mv.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		return mv;
	}
}
