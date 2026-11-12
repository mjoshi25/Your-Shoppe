package com.asmj.marketplace.delivery;

import java.math.BigDecimal;

public record GoogleRouteResult(BigDecimal distanceKm, String encodedPolyline) { }
