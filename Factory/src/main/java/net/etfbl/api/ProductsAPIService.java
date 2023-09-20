package net.etfbl.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import net.etfbl.model.Product;
import net.etfbl.util.MyLogger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Path("/products")
public class ProductsAPIService {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		String jedisHost = loadProperties().getProperty("JEDIS_HOST");
		
		try (JedisPool pool = new JedisPool(jedisHost); Jedis jedis = pool.getResource()) {
			Set<String> keys = jedis.keys("*");
			
			ArrayList<Product> products = new ArrayList<>();
			
			for (String key : keys) {
	            String password = jedis.get(key);
	            products.add(new Product(Integer.parseInt(key), password));
	        }
			
			return Response.status(Response.Status.OK).entity(products).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProduct(Product product) {
		String jedisHost = loadProperties().getProperty("JEDIS_HOST");
		
		try (JedisPool pool = new JedisPool(jedisHost); Jedis jedis = pool.getResource()) {
			if (jedis.exists(product.getProductId()+"")) {
				return Response.status(400).entity("Invalid argument (invalid request payload)").build();
			} else {
				jedis.set(product.getProductId()+"", product.getProductName());
				return Response.status(Response.Status.OK).entity(true).build();
			}
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(Product product) {
		String jedisHost = loadProperties().getProperty("JEDIS_HOST");
		
		try (JedisPool pool = new JedisPool(jedisHost); Jedis jedis = pool.getResource()) {
			if (!jedis.exists(product.getProductId()+"")) {
				return Response.status(Response.Status.NOT_FOUND).build();
			} else {
				jedis.set(product.getProductId()+"", product.getProductName());
				return Response.status(Response.Status.OK).entity(true).build();
			}
		}
	}
	
	@DELETE
	@Path("/{productId}")
	public Response deleteUser(@PathParam("productId") String productId) {
		String jedisHost = loadProperties().getProperty("JEDIS_HOST");
		
		try (JedisPool pool = new JedisPool(jedisHost); Jedis jedis = pool.getResource()) {
			if (!jedis.exists(productId)) {
				return Response.status(Response.Status.NOT_FOUND).build();
			} else {
				jedis.del(productId);
				return Response.status(Response.Status.OK).entity(true).build();
			}
		}
	}
	
	private Properties loadProperties() {
		Properties properties = new Properties();
		File file = new File("../../ProjektniZadatak/Factory/src/main/java/resources/config.properties");

		try {
			properties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		} catch (IOException e) {
			MyLogger.LOGGER.log(Level.SEVERE, "An exception occurred", e);
		}
		return properties;
	}
}
