package y.semina.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import y.semina.model.User;
import y.semina.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User firstTestUser;
    private User secondTestUser;

    @BeforeEach
    void setUp() {
        firstTestUser = new User("Иван Иванов", "ivan@mail.com");
        firstTestUser.setId(1L);

        secondTestUser = new User("Мария Петрова", "maria@mail.com");
        secondTestUser.setId(2L);
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        List<User> users = Arrays.asList(firstTestUser, secondTestUser);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Иван Иванов"))
                .andExpect(jsonPath("$[0].email").value("ivan@mail.com"))
                .andExpect(jsonPath("$[1].name").value("Мария Петрова"))
                .andExpect(jsonPath("$[1].email").value("maria@mail.com"))
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].orders").doesNotExist());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyArray() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_WithExistingId_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(firstTestUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value("ivan@mail.com"))
                .andExpect(jsonPath("$.orders").exists());

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void createUser_WithValidData_ShouldCreateAndReturnUser() throws Exception {
        User newUser = new User("Новый Пользователь", "new@mail.com");
        newUser.setId(3L);

        when(userService.createUser(any(User.class))).thenReturn(newUser);

        String userJson = """
            {
                "name": "Новый Пользователь",
                "email": "new@mail.com"
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новый Пользователь"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateAndReturnUser() throws Exception {
        User updatedUser = new User("Обновленное Имя", "updated@mail.com");
        updatedUser.setId(1L);

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        String updateJson = """
            {
                "name": "Обновленное Имя",
                "email": "updated@mail.com"
            }
            """;

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновленное Имя"))
                .andExpect(jsonPath("$.email").value("updated@mail.com"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void deleteUser_WithExistingId_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void jsonView_ShouldShowDifferentFields() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(firstTestUser));
        when(userService.getUserById(1L)).thenReturn(firstTestUser);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].id").doesNotExist())
                .andExpect(jsonPath("$[0].orders").doesNotExist());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.orders").exists());
    }

}
