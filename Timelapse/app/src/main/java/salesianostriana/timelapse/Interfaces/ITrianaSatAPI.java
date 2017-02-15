package salesianostriana.timelapse.Interfaces;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import salesianostriana.timelapse.pojos.API.FotoInfo;
import salesianostriana.timelapse.pojos.hrefAPI.ListProyectos;

/**
 * Created by rtovar on 07/02/2017.
 */

/**
 * Interfaz usada por retrofit para subir imágenes
 */
public interface ITrianaSatAPI {

    String ENDPOINT_API = "http://trianasat2-salesianostriana.rhcloud.com/";
    String ENDPOINT_SALESIANOS = "http://www.salesianos-triana.com/";
    String ENDPOINT_API_LOCAL = "http://172.27.0.136:8080";

    @GET("proyectos/search/findByToken")
    Call<ListProyectos> obtenerProyecto(@Query("token") String token);

    @POST("dam/trianasat/")
    @Multipart
    Call<ResponseBody> subirFoto(@Part MultipartBody.Part file);

    @Headers("Accept: application/json")
    @POST("timelapse")
    Call<ResponseBody> subirFotoInfo(@Body FotoInfo fotoInfo);
}