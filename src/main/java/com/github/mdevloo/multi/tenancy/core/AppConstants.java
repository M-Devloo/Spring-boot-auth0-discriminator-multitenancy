package com.github.mdevloo.multi.tenancy.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

  public static final String API = "/api";
  public static final String INVENTORY_URL = API + "/inventory";

}
