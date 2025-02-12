package github.grif.chain;

import org.springframework.stereotype.Component;

@Component
public class TestBaseHandler extends BaseHandler {

	@Override
	public boolean handle(String request) {
		System.out.println("Test Request handled: " + request);
		return true;
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
