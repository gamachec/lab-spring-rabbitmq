package fr.maedhros.lab.evenement.metier;

import fr.maedhros.lab.evenement.annotation.EvenementAvecRejeuPlanifie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EvenementAvecRejeuPlanifie(delaisSecondes = 10)
public class EvenementSimple implements Evenement {

    private String id;

    private String message;
}
