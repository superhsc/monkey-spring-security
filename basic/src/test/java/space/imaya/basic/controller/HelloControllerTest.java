package space.imaya.basic.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class HelloControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testUnauthenticatedUser() throws Exception {
		mockMvc.perform(get("/hello"))
				.andExpect(status().isUnauthorized());
	}


	@Test
	@WithMockUser(username = "admin", roles = {"USER","ADMIN"})
	public void testAuthenticatedUser() throws Exception {
		mockMvc.perform(get("/hello"))

				.andExpect(content().string("Hello"))
				.andExpect(status().isOk());
	}


}
