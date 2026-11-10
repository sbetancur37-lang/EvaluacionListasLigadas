import java.io.*;

public class ListaLigadaParadas {
    private Parada cabeza;
    private int tamano;

    public ListaLigadaParadas() {
        this.cabeza = null;
        this.tamano = 0;
    }

    public void agregarParada(String nombre, double latitud, double longitud, String tiempo, String desc) {
        Parada nueva = new Parada(nombre, latitud, longitud, tiempo, desc);
        if (cabeza == null) {
            cabeza = nueva;
        } else {
            Parada actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nueva);
        }
        tamano++;
    }

    public Parada obtenerParada(int index) {
        if (index < 0 || index >= tamano) return null;
        Parada actual = cabeza;
        for (int i = 0; i < index; i++) {
            actual = actual.getSiguiente();
        }
        return actual;
    }

    public boolean modificarParada(int index, String nombre, double lat, double lon, String tiempo, String desc) {
        Parada p = obtenerParada(index);
        if (p != null) {
            p.setNombre(nombre);
            p.setLatitud(lat);
            p.setLongitud(lon);
            p.setTiempoEstimado(tiempo);
            p.setDescripcion(desc);
            return true;
        }
        return false;
    }

    public boolean eliminarParada(int index) {
        if (index < 0 || index >= tamano) return false;
        if (index == 0) {
            cabeza = cabeza.getSiguiente();
        } else {
            Parada anterior = obtenerParada(index - 1);
            if (anterior != null && anterior.getSiguiente() != null) {
                anterior.setSiguiente(anterior.getSiguiente().getSiguiente());
            }
        }
        tamano--;
        return true;
    }

    public String[] obtenerNombresParadas() {
        String[] nombres = new String[tamano];
        Parada actual = cabeza;
        int i = 0;
        while (actual != null) {
            nombres[i++] = actual.toString();
            actual = actual.getSiguiente();
        }
        return nombres;
    }

    public double calcularDistanciaTotal() {
        if (cabeza == null || cabeza.getSiguiente() == null) return 0.0;
        
        double distanciaTotal = 0.0;
        Parada actual = cabeza;
        
        while (actual.getSiguiente() != null) {
            Parada siguiente = actual.getSiguiente();
            double lat1 = actual.getLatitud();
            double lon1 = actual.getLongitud();
            double lat2 = siguiente.getLatitud();
            double lon2 = siguiente.getLongitud();
            
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                       Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distanciaKm = 6371 * c;
            
            distanciaTotal += distanciaKm;
            actual = siguiente;
        }
        return distanciaTotal;
    }

    public void guardarRutaJSON(String rutaArchivo) throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        Parada actual = cabeza;
        while (actual != null) {
            json.append("  {\n");
            json.append("    \"nombre\": \"").append(actual.getNombre()).append("\",\n");
            json.append("    \"latitud\": ").append(actual.getLatitud()).append(",\n");
            json.append("    \"longitud\": ").append(actual.getLongitud()).append(",\n");
            json.append("    \"tiempoEstimado\": \"").append(actual.getTiempoEstimado()).append("\",\n");
            json.append("    \"descripcion\": \"").append(actual.getDescripcion()).append("\"\n");
            json.append("  }");
            if (actual.getSiguiente() != null) {
                json.append(",");
            }
            json.append("\n");
            actual = actual.getSiguiente();
        }
        json.append("]");

        try (FileWriter file = new FileWriter(rutaArchivo)) {
            file.write(json.toString());
        }
    }

    public void cargarRutaJSON(String rutaArchivo) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo));
        StringBuilder contenido = new StringBuilder();
        String linea;
        while ((linea = reader.readLine()) != null) {
            contenido.append(linea.trim());
        }
        reader.close();

        cabeza = null;
        tamano = 0;

        String limpio = contenido.toString();
        if (limpio.equals("[]") || limpio.isEmpty()) return;

        limpio = limpio.substring(1, limpio.length() - 1);
        String[] objetos = limpio.split("\\},\\s*\\{");

        for (String obj : objetos) {
            obj = obj.replace("{", "").replace("}", "").replace("\"", "");
            String[] campos = obj.split(",");
            
            String nombre = "";
            double lat = 0.0;
            double lon = 0.0;
            String tiempo = "";
            String desc = "";

            for (String campo : campos) {
                String[] par = campo.split(":");
                if (par.length < 2) continue;
                String clave = par[0].trim();
                String valor = par[1].trim();

                switch (clave) {
                    case "nombre": nombre = valor; break;
                    case "latitud": lat = Double.parseDouble(valor); break;
                    case "longitud": lon = Double.parseDouble(valor); break;
                    case "tiempoEstimado": tiempo = valor; break;
                    case "descripcion": desc = valor; break;
                }
            }
            agregarParada(nombre, lat, lon, tiempo, desc);
        }
    }
}