package salesianostriana.timelapse.Interfaces;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import salesianostriana.timelapse.Pojos.Foto;
import salesianostriana.timelapse.Pojos.hrefAPI.ListProyectos;

/**
 * Created by rtovar on 07/02/2017.
 */

public interface ITrianaSatAPI {

    String ENDPOINT_API = "http://172.27.0.136:8080/"; //TODO
    String ENDPOINT_SALESIANOS = "http://www.salesianos-triana.com/"; //TODO
    String TOKEN = "asdfg435cdghs79846h741asdfg435cdg";


    @GET("proyectos/search/findByToken")
    Call<ListProyectos> obtenerProyecto(@Query("token") String token);

    @POST("dam/trianasat/")
    @Multipart
    Call<ResponseBody> subirDatosFoto(@Part MultipartBody.Part file);



    //@POST("gps")
    //Call<Gps> upload(@Body Gps datos);


}
