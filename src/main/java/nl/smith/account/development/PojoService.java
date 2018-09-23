package nl.smith.account.development;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class PojoService {
	public void savePojo(@Valid RawPojo pojo) {
		System.out.println("Valid");
	}
}
