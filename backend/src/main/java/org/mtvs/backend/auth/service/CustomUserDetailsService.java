package org.mtvs.backend.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mtvs.backend.auth.model.CustomUserDetails;
import org.mtvs.backend.auth.model.User;
import org.mtvs.backend.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository memberRepository;


    public UserDetails loadUserByUsername(long id) throws UsernameNotFoundException {
        User member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

        return new CustomUserDetails(member);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ID로 사용자를 찾는 경우 (JWT에서 사용)
        try {
            Long id = Long.parseLong(username);
            return loadUserByUsername(id);
        } catch (NumberFormatException e) {
            // username으로 사용자를 찾는 경우
            User member = memberRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다: " + username));
            return new CustomUserDetails(member);
        }
    }
}
