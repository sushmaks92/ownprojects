package fibinacciseries;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FibonacciTest {

	Fibonacci f;

	@Before
	public void setUp() throws Exception {
		f = new Fibonacci();
	}

	@Test
	public void testFibbonacciWithOneAsInput() {

		Assert.assertEquals(1, Fibonacci.fibonacciSeries(1));
	}

	@Test
	public void testFibbonacciWithPositiveIntegerAsInput() {

		Assert.assertEquals(1, Fibonacci.fibonacciSeries(3));
	}
}
