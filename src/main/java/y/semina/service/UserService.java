package y.semina.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import y.semina.exeption.UserNotFoundException;
import y.semina.model.User;
import y.semina.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = findById(id);

            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        findById(id);
        userRepository.deleteById(id);
    }

    private User findById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + id + " не существует"));
    }

}
