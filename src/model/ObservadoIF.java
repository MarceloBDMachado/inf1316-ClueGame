package model;

public interface ObservadoIF {
    void add(ObservadorIF o);
    void remove(ObservadorIF o);
    void notificarObservadores();
}