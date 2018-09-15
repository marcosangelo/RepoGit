package br.com.repogit.Dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.repogit.Model.Repositorio;

@Dao
public interface RepositorioDao {
    @Query("SELECT * FROM Repositorio")
    List<Repositorio> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRepository(Repositorio repository);

    @Query("DELETE FROM Repositorio")
    void deleteAll();

    @Update
    void updateRepository(Repositorio repository);
}
