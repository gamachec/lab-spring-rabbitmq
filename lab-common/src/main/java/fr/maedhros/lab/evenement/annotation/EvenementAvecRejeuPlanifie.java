package fr.maedhros.lab.evenement.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.maedhros.lab.evenement.metier.Evenement;

/**
 * A placer sur une classe implémentant {@link fr.maedhros.lab.evenement.metier.Evenement} pour permettre d'utiliser le rejeu planifié via
 * {@link fr.maedhros.lab.evenement.service.EvenementService#planifierRejeuEvenement(Evenement)}
 * <p>
 * Cette classe induit la création d'une queue supplémentaire suffixé de ".retry" pour permettre le retry planifié.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface EvenementAvecRejeuPlanifie {

    /**
     * Delais à appliquer au rejet, en secondes
     *
     * @return
     */
    int delaisSecondes() default 5;
}
