package org.kew.stringmod.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A LibraryRegister serves as a communication interface to MatchConf;
 * any annotated Matcher/Transformer/Reporter class will be available
 * in MatchConf.
 *
 * The `category` keyword is one of ('matchers', 'transformers', 'reporters')
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface LibraryRegister {

    String category();

}
