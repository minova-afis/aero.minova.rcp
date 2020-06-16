package aero.minova.rcp.plugin1;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobFunction;
import org.eclipse.e4.ui.di.UISynchronize;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.plugin1.model.Column;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;
import aero.minova.rcp.plugin1.model.ValueDeserializer;
import aero.minova.rcp.plugin1.model.ValueSerializer;

public class Activator implements BundleActivator {

	// get UISynchronize injected as field
	@Inject
	UISynchronize sync;

	@Override
	public void start(BundleContext context) throws Exception {
		test();
		// more code
		@SuppressWarnings("unused")
		IJobFunction jobFunction = new IJobFunction() {

			@Override
			public IStatus run(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				return null;
			}
		};

//		Job job;
//		job = job.create("Read OrderReceiver", (IJobFunction) monitor -> {
//			
//			return IStatus.OK;
//		});
//		
//		job = Job.create("Update table", (ICoreRunnable) monitor -> {
//		try {
//			Client client = ClientBuilder.newClient();
////				Entity<String> payload = Entity.json("{  'productPair': 'BTCUSD'}");
//			Response response = client.target("http://10.242.2.4:10002").path("/movement/1")
//					.request(MediaType.APPLICATION_JSON_TYPE).get();
////				Response response = client.target("http://localhost:8080").path("/orderreceiver")
////						.request().get();
//
//			System.out.println("status: " + response.toString());
//			System.out.println("entity: " + response.readEntity(String.class));
//		} catch (Exception e) {
//			System.out.println(e);
//		}

		// If you want to update the UI
//		sync.asyncExec(() -> {
//			// do something in the user interface
//			// e.g. set a text field
//		});
//		});
//
//		// Start the Job
//		job.schedule();	
	}

	private void test() {
		Table t = new Table();
		t.setName("OrderReceiver");
		t.addColumn(new Column("KeyLong", DataType.INTEGER));
		t.addColumn(new Column("KeyText", DataType.STRING));
		t.addColumn(new Column("Description", DataType.STRING));
		t.addColumn(new Column("LastDate", DataType.TIMESTAMP));
		t.addColumn(new Column("ValidUntil", DataType.DATE));

		Row r;
		r = new Row();
		r.addValue(new Value(1));
		r.addValue(null);
//		r.addValue(new Value(23.5));
		r.addValue(new Value("Wilfried Saak"));
		r.addValue(new Value(Instant.now()));
		r.addValue(new Value(ZonedDateTime.now()));
		t.addRow(r);
		r = new Row();
		r.addValue(new Value(123.45));
		r.addValue(new Value("THEUERERG"));
		r.addValue(new Value("Gudrun Theuerer"));
		r.addValue(new Value(Instant.now()));
		r.addValue(new Value(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin"))));
		t.addRow(r);

		Gson gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();
		String s = gson.toJson(t);
		System.out.println(s);
		t = gson.fromJson(s, Table.class);
		System.out.println(gson.toJson(t));
		System.out.println(t.getName());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
//		org.glassfish.jersey.client.JerseyClientBuilder c;
	}

}
