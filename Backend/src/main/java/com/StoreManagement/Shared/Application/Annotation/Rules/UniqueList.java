package com.StoreManagement.Shared.Application.Annotation.Rules;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueList {
    Unique[] value();
}
