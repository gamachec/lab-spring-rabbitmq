package fr.maedhros.lab.evenement.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A placer sur une méthode consommant des {@link fr.maedhros.lab.evenement.metier.Evenement}
 * La méthode doit avoir un (et un seul) paramètre implémentant {@link fr.maedhros.lab.evenement.metier.Evenement}
 * <p>
 * La queue sur laquelle les messages sont récupérés est automatiquement détectée en fonction du type du paramètre de la méthode.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface EvenementHandler {

    /**
     * @return Nombre de threads consommateurs à démarrer
     */
    int threads() default 1;
}
