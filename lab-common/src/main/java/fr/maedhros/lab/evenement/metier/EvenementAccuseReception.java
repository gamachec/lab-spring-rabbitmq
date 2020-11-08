package fr.maedhros.lab.evenement.metier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvenementAccuseReception implements Evenement {

    private String message;

    private String app;
}
