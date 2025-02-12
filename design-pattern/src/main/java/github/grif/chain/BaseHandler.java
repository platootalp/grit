package github.grif.chain;

import org.springframework.stereotype.Component;

@Component
public class BaseHandler implements Handler<String> {

	@Override
	public boolean handle(String request) {
		System.out.println("Request handled: " + request);
		return true;
	}

	@Override
	public String getMark() {
		return "controller";
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
