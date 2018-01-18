/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agromarketday.ussd.exception;

/**
 *
 * @author smallgod
 */
public class EmptyStringException extends RuntimeException  {

        public EmptyStringException() {
        }

        public EmptyStringException(String message) {
            super(message);
        }

        public EmptyStringException(Throwable cause) {
            super(cause);
        }

        public EmptyStringException(String message, Throwable cause) {
            super(message, cause);
        }
    }
