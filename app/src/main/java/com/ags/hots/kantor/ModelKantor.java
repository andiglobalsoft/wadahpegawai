package com.ags.hots.kantor;

public class ModelKantor {
    String id_kantor;
    String judul;
    String deskripsi;
    String foto;
    String latitude;
    String longitude;

    public ModelKantor() {
    }

    public String getId_kantor() {
        return id_kantor;
    }

    public void setId_kantor(String id_kantor) {
        this.id_kantor = id_kantor;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
