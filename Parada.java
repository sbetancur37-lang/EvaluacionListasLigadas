public class Parada {
    private String nombre;
    private double latitud;
    private double longitud;
    private String tiempoEstimado;
    private String descripcion;
    private Parada siguiente;

    public Parada(String nombre, double latitud, double longitud, String tiempoEstimado, String descripcion) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tiempoEstimado = tiempoEstimado;
        this.descripcion = descripcion;
        this.siguiente = null;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getTiempoEstimado() { return tiempoEstimado; }
    public void setTiempoEstimado(String tiempoEstimado) { this.tiempoEstimado = tiempoEstimado; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Parada getSiguiente() { return siguiente; }
    public void setSiguiente(Parada siguiente) { this.siguiente = siguiente; }

    @Override
    public String toString() {
        return nombre + " (Llegada: " + tiempoEstimado + ")";
    }
}