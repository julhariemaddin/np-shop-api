package com.ecommerce.np_shop.config;

import com.ecommerce.np_shop.entity.Account;
import com.ecommerce.np_shop.entity.Role;
import com.ecommerce.np_shop.repo.AccountRepository;
import com.ecommerce.np_shop.repo.RoleRepository;
import com.ecommerce.np_shop.security.AccountDetails;
import com.ecommerce.np_shop.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class StartRunner implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void run(String... args) throws Exception {
        //--Start-Data

        //--initialize role
        if(roleRepository.count() == 0) {
            Role admin =  new Role();
            Role user =  new Role();
            Role superAdmin =  new Role();
            superAdmin.setName("ROLE_SUPER_ADMIN");
            admin.setName("ROLE_ADMIN");
            user.setName("ROLE_USER");
            roleRepository.save(superAdmin);
            roleRepository.save(admin);
            roleRepository.save(user);
        }
        //--intialize SuperAdmin account
        if(accountRepository.count() == 0){
            Account account = new Account();
            account.setUsername("SuperAdmin");
            account.setPassword(passwordEncoder.encode("SuperAdminPassword"));
            account.getRoles().add(roleRepository.findByName("ROLE_SUPER_ADMIN"));
            account.getRoles().add(roleRepository.findByName("ROLE_ADMIN"));
            accountRepository.save(account);
        }

    if (!accountRepository.existsByUsername("Lol")) {
      Account account = new Account();
      account.setUsername("Lol");
      account.setPassword(passwordEncoder.encode("LolPassword"));
      account.getRoles().add(roleRepository.findByName("ROLE_USER"));
      Account savedAccount  = accountRepository.save(account);
    }

        //End-Data--
        /*
        addSpace(100);
        System.out.println("""
                
                ███╗   ██╗██████╗     ███████╗██╗  ██╗ ██████╗ ██████╗
                ████╗  ██║██╔══██╗    ██╔════╝██║  ██║██╔═══██╗██╔══██╗
                ██╔██╗ ██║██████╔╝    ███████╗███████║██║   ██║██████╔╝
                ██║╚██╗██║██╔═══╝     ╚════██║██╔══██║██║   ██║██╔═══╝
                ██║ ╚████║██║         ███████║██║  ██║╚██████╔╝██║
                ╚═╝  ╚═══╝╚═╝         ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═╝
                """);
        System.out.println("Initializing Required Data");

        String[] steps = {
                "Role initializing",
                "Super Admin initializing",
        };
        String[] stepsCompleted = {
               "Role initialized completed",
                "Super Admin initialized completed",
        };
        //Fake Loading screen
        for (int i = 0; i<steps.length;i++) {
            int progress = 0;
            System.out.printf("%n--%s--%n",steps[i]);

            for (int j = 0; j <= 20; j++) {
                String bar = (j==0 ? "" : "=".repeat(j))+">" + " ".repeat(20 - j);

                System.out.printf(
                        "\r[%s] %s %d%%",
                        bar,
                        steps[i]
                        ,
                        progress + (j*5)
                );

                Thread.sleep(40);
            }


            System.out.printf(
                    "\r[✓] %s",
                    stepsCompleted[i]
            );

            Thread.sleep(2000);
        }
        System.out.println("\n\nAll data has been initialized");

        addSpace(2);*/
    }

    private static void addSpace(int spaceLine){
        for(int i=0;i<spaceLine;i++){
            System.out.println();
        }

    }
}
