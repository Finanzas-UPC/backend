package com.upc.finanzas.iam.infrastructure.hashing.bcrypt;

import com.upc.finanzas.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface BCryptHashingService extends HashingService, PasswordEncoder {
}