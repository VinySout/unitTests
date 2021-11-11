package br.com.unitTests.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.com.unitTests.entidades.Usuario;


public class AssertTest {

	@Test
	public void test() {
		Assert.assertTrue(true);
		Assert.assertFalse(false);
		
		Assert.assertEquals("Erro de comparação", 1, 1);
		Assert.assertEquals(0.51021, 0.51021, 0.0001);
		
		int i = 5;
		Integer i2 = 5;
		Assert.assertEquals(Integer.valueOf(i), i2);
		Assert.assertEquals(i, i2.intValue());
		
		Assert.assertEquals("bola", "bola");
		Assert.assertEquals("bola".toLowerCase() , "Bola".toLowerCase());
		Assert.assertTrue("bola".equalsIgnoreCase("Bola"));
		Assert.assertTrue("bola".startsWith("bo"));
		
		Usuario u1 = new Usuario("Usuario 1");
		Usuario u2 = new Usuario("Usuario 1");
		Usuario u3 = null;
		
		//Método equals sobreescrito na classe Usuário.
		Assert.assertEquals(u1, u2);
		
		//verificando a mesma instância u1 e u2
		Assert.assertSame(u2, u2);
		
		Assert.assertNull(u3);
	}
}
