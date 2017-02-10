package salesianostriana.timelapse;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import salesianostriana.timelapse.Pojos.Foto;

/**
 * Created by Isabel on 06/02/2017.
 */

public class FotosDatabase {
    //CONSTANTES, columnas
    public static final String ID = "_id";
    public static final String NOMBRE = "nombre";
    public static final String FECHA_MILISEGUNDOS = "fecha";
    public static final String BATERIA = "bateria";
    public static final String SUBIDA = "subida";


    static final String TAG = "FotosDatabase";
    static final String DATABASE_NAME = "FotosDatabase";
    static final String DATABASE_TABLE = "foto";
    static final int DATABASE_VERSION = 1;


    //Setencia para crear la tabla libros
    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" + ID + " integer primary key autoincrement, "
            + NOMBRE + " text, "
            + FECHA_MILISEGUNDOS + " integer, "
            + BATERIA + " real, "
            + SUBIDA + " integer);";

    DatabaseHelper mDbHelper;
    SQLiteDatabase myBD;

    final Context mCtx;

    public FotosDatabase(Context ctx) {
        this.mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);//Ejecutamos la setencia de crear la tabla
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " //$NON-NLS-1$//$NON-NLS-2$
                    + newVersion + ", which will destroy all old data"); //$NON-NLS-1$
            //db.execSQL("DROP TABLE IF EXISTS usersinfo"); //$NON-NLS-1$
            onCreate(db);
        }
    }


    /**
     * Añade un registro a la base de datos
     */
    public long insert(Foto foto) {
        ContentValues columnas = new ContentValues();//Columnas de la tabla

        columnas.put(NOMBRE, foto.getNombre());
        columnas.put(FECHA_MILISEGUNDOS, foto.getFechaMilisegundos());
        columnas.put(BATERIA, foto.getBateria());
        columnas.put(SUBIDA, foto.getSubida());

        return this.myBD.insert(DATABASE_TABLE, null, columnas);
    }


    /**
     * Elimina un registro en la base de datos     *
     *
     * @param rowId ID del registro que debe eliminar
     * @return
     */
    public boolean delete(long rowId) {

        return this.myBD.delete(DATABASE_TABLE, ID + "=" + rowId, null) > 0; //$NON-NLS-1$
    }

    /**
     * Actualiza un registro
     */
    public boolean update(Foto foto) {
        ContentValues columnas = new ContentValues();//Columnas de la tabla

        columnas.put(NOMBRE, foto.getNombre());
        columnas.put(FECHA_MILISEGUNDOS, foto.getFechaMilisegundos());
        columnas.put(BATERIA, foto.getBateria());
        columnas.put(SUBIDA, foto.getSubida());

        return this.myBD.update(DATABASE_TABLE, columnas, ID + "=" + foto.getId(), null) > 0; //$NON-NLS-1$
    }

    /**
     * Actualiza una foto para cambiar si está subida o no
     * Subida = 1, NO subida = 0
     */
    public boolean updateSubida(long id, int subida) {
        ContentValues columnas = new ContentValues();//Columnas de la tabla

        columnas.put(SUBIDA, subida);

        return this.myBD.update(DATABASE_TABLE, columnas, ID + "=" + id, null) > 0; //$NON-NLS-1$
    }


    /**
     * Devuelve todos los registros guardados en la base de datos
     */
    public List<Foto> getAll() {

        List<Foto> listFotos = new ArrayList<>();

        Cursor cursor = this.myBD.rawQuery("SELECT * FROM " + DATABASE_TABLE + " ORDER BY subida", null);

        if (cursor.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE));
                Long fecha = cursor.getLong(cursor.getColumnIndexOrThrow(FECHA_MILISEGUNDOS));
                Double bateria = cursor.getDouble(cursor.getColumnIndexOrThrow(BATERIA));
                int subida = cursor.getInt(cursor.getColumnIndexOrThrow(SUBIDA));

                listFotos.add(new Foto(id, path, fecha, bateria, subida));
            } while (cursor.moveToNext());
        }

        return listFotos;
    }

    /**
     * Devuelve un determinado registro
     *
     * @param rowId ID del registro que debe devolver
     */
    public Foto get(long rowId) {

        Cursor cursor = myBD.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE _id = " + rowId, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
        String path = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE));
        Long fecha = cursor.getLong(cursor.getColumnIndexOrThrow(FECHA_MILISEGUNDOS));
        Double bateria = cursor.getDouble(cursor.getColumnIndexOrThrow(BATERIA));
        int subida = cursor.getInt(cursor.getColumnIndexOrThrow(SUBIDA));

        return new Foto(id, path, fecha, bateria, subida);
    }

    /**
     * Devuelve el número de registros que tiene la tabla
     */
    public int getCount() throws SQLException {

        Cursor cursor = myBD.rawQuery("SELECT count(*) FROM " + DATABASE_TABLE, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    /**
     * Consulta las fotos subidas
     */
    public List<Foto> getSubidas() throws SQLException {

        List<Foto> listFotos = new ArrayList<>();

        Cursor cursor = this.myBD.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE subida = 1", null);

        if (cursor.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE));
                Long fecha = cursor.getLong(cursor.getColumnIndexOrThrow(FECHA_MILISEGUNDOS));
                Double bateria = cursor.getDouble(cursor.getColumnIndexOrThrow(BATERIA));
                int subida = cursor.getInt(cursor.getColumnIndexOrThrow(SUBIDA));

                listFotos.add(new Foto(id, path, fecha, bateria, subida));
            } while (cursor.moveToNext());
        }

        return listFotos;
    }

    /**
     * Consulta las fotos no subidas
     */
    public List<Foto> getNoSubidas() throws SQLException {

        List<Foto> listFotos = new ArrayList<>();

        Cursor cursor = this.myBD.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE subida = 0", null);

        if (cursor.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE));
                Long fecha = cursor.getLong(cursor.getColumnIndexOrThrow(FECHA_MILISEGUNDOS));
                Double bateria = cursor.getDouble(cursor.getColumnIndexOrThrow(BATERIA));
                int subida = cursor.getInt(cursor.getColumnIndexOrThrow(SUBIDA));

                listFotos.add(new Foto(id, path, fecha, bateria, subida));
            } while (cursor.moveToNext());
        }

        return listFotos;
    }
    public Foto getNoSubida() throws SQLException {

        List<Foto> listFotos = new ArrayList<>();

        Cursor cursor = this.myBD.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE subida = 0 LIMIT 1", null);

        if (cursor.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(NOMBRE));
                Long fecha = cursor.getLong(cursor.getColumnIndexOrThrow(FECHA_MILISEGUNDOS));
                Double bateria = cursor.getDouble(cursor.getColumnIndexOrThrow(BATERIA));
                int subida = cursor.getInt(cursor.getColumnIndexOrThrow(SUBIDA));

                listFotos.add(new Foto(id, path, fecha, bateria, subida));
            } while (cursor.moveToNext());
        }

        return listFotos.get(0);
    }



    /**
     * Abre la base de datos en modo escritura
     */
    public FotosDatabase openWrite() throws SQLException {
        //this.mDbHelper = new DatabaseHelper(this.mCtx);
        myBD = this.mDbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Abre la base de datos en modo lectura
     */
    public FotosDatabase openRead() throws SQLException {
        // this.mDbHelper = new DatabaseHelper(this.mCtx);
        myBD = mDbHelper.getReadableDatabase();
        return this;
    }

    /**
     * Cierra la base de datos
     */
    public void close() {
        this.mDbHelper.close();
    }
}
