package com.personapplication.stijn.marsroverphotos.Domain;

//When the application calls a Loader, it has the option to either Initialize or restart the loader
//RESTART: Restart the loader, forces data to reload
//INIT: Standard initialization, loader will return existing data if present
public enum LoaderMode {
    RESTART, INIT
}
