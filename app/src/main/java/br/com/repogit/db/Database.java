package br.com.repogit.db;

import android.arch.persistence.room.RoomDatabase;

import br.com.repogit.Dao.RepositorioDao;
import br.com.repogit.Model.Repositorio;

@android.arch.persistence.room.Database(entities = {Repositorio.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract RepositorioDao getRepositoryDao();

}