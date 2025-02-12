package github.grif;

import github.grif.chain.ChainContext;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Test;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {

    @Autowired
    private ChainContext chainContext;

    @Test
    public void testChainContext() {
        chainContext.executeChain("controller", "niubi");
    }

}
