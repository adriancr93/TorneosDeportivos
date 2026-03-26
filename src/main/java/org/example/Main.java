package org.example;

import javafx.application.Application;
import org.example.controller.TorneoController;
import org.example.view.LoginView;

public class Main {
    public static void main(String[] args) {
        boolean usarGui = args != null && args.length > 0 && "--gui".equalsIgnoreCase(args[0]);
        if (usarGui) {
            Application.launch(LoginView.class, args);
            return;
        }

        TorneoController controller = new TorneoController();
        controller.iniciar();
    }
}
