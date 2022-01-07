package aero.minova.server.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.runners.MethodSorters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.dataservice.internal.DataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.ColumnSerializer;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

/**
 * Integration test for the data service
 * 
 * @author Lars
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DataServiceTest {

	private String username = "admin";
	private String password = "rqgzxTf71EAx8chvchMi";
	// Dies ist unser üblicher Server, von welchen wir unsere Daten abfragen
	private String server = "http://publictest.minova.com:17280/cas";

	DataService dataService;

	@BeforeEach
	void configureDataService(@TempDir Path path) {
		URI uri = path.toUri();
		String stringUri = uri.toString();
		dataService = new DataService();
		dataService.setCredentials(username, password, server, URI.create(stringUri));
	}

	@Test
	@DisplayName("Simple test to easily debug the created URI")
	void canCreateUri(@TempDir Path path) {
		URI uri = path.toUri();
		String stringUri = uri.toString() + File.separator;
		assertNotNull(uri);
		assertTrue(stringUri.startsWith("file"));
		assertFalse(stringUri.endsWith(";"));
	}

	@Test
	@DisplayName("Ensures the server returns not 200 for files that do not exit")
	void ensureThatWeThrowAnExceptionForMissingFiles() {
		assertThrows(RuntimeException.class, () -> {
			dataService.getServerHashForFile("test").join();
		});
	}

	@Test
	@DisplayName("Ensures that the server can hash application.mdi")
	void hashApplicationMdi() {
		String join = dataService.getServerHashForFile("application.mdi").join();
		assertNotNull(join);
	}

	@Test
	@DisplayName("Get application.mdi twice should load from cache")
	void receiveTwiceTheSameFileShouldLoadFromCache() {
		// first call should download and create the cached file
		String firstVersion = dataService.getHashedFile("application.mdi").join();
		// second call should read the cached file
		String secondVersion = dataService.getHashedFile("application.mdi").join();

		// TODO Check that really the hash version was used, maybe Mockito can be used
		// to wrap the data service?
		assertEquals(firstVersion, secondVersion);
	}

	@Test
	@DisplayName("Ensure we can download aero.minova.invoice.helper")
	void ensureDownloadOfPlugin() {
		boolean hashedZip = dataService.getHashedZip("plugins.zip");
		// TODO Check that really the hash version was used, maybe Mockito can be used
		// to wrap the data service?
		assertTrue(hashedZip);
		Path storagePath = dataService.getStoragePath();
		Path path = Paths.get(storagePath.toString(), "plugins");
		boolean exists = Files.exists(path, LinkOption.NOFOLLOW_LINKS);
		assertTrue(exists, "Unzipped directory not available on the local file system");
		try (Stream<Path> list = Files.list(path)) {
			long count = list.filter(f -> f.toString().contains("aero.minova.invoice.helper")).count();
			assertEquals(1, count, "Jar file nicht oder mehrfach vorhanden");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("Split String from class to plugin name")
	void validateThatPluginForHelperClassIsNamedCorretly() {
		String className = "aero.minova.invoice.helper.InvoiceHelper";
		int lastIndexOf = className.lastIndexOf('.');
		String pluginName = className.substring(0, lastIndexOf);

		assertEquals("aero.minova.invoice.helper", pluginName, "Hey looks like we do not know how to split strings");
	}

	@Test
	@DisplayName("Extract Errormessage to translate")
	void extractErrorMessageToTranslate() {
		Gson gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.registerTypeAdapter(Column.class, new ColumnSerializer()) //
				.setPrettyPrinting() //
				.create();

		String body = "{\"resultSet\":{\"name\":\"Error\",\"columns\":[{\"name\":\"International Message\",\"type\":\"STRING\"}],\"rows\":[{\"values\":[\"s-ADO | 25 | msg.sql.51103 @p tUnit.Description.6 @p tUnit.Description.14 | Einheit kann nicht in Einheit der Leistungsart umgerechnet werden\"]}],\"returnErrorMessage\":{\"detailsMessage\":\"com.microsoft.sqlserver.jdbc.SQLServerException: ADO | 25 | msg.sql.51002 | Dieser Vorgang wurde bereits storniert\",\"cause\":\"aero.minova.core.application.system.domain.ProcedureException: aero.minova.core.application.system.domain.ProcedureException: com.microsoft.sqlserver.jdbc.SQLServerException: ADO | 25 | msg.sql.51002 | Dieser Vorgang wurde bereits storniert\",\"trace\":[\"aero.minova.core.application.system.domain.ProcedureException: aero.minova.core.application.system.domain.ProcedureException: com.microsoft.sqlserver.jdbc.SQLServerException: ADO | 25 | msg.sql.51002 | Dieser Vorgang wurde bereits storniert\",\"aero.minova.core.application.system.controller.SqlProcedureController.executeProcedure(SqlProcedureController.java:141)\",\"jdk.internal.reflect.GeneratedMethodAccessor64.invoke(Unknown Source)\",\"java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)\",\"java.base/java.lang.reflect.Method.invoke(Unknown Source)\",\"org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:190)\",\"org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)\",\"org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:106)\",\"org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:879)\",\"org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:793)\",\"org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\",\"org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1040)\",\"org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:943)\",\"org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)\",\"org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909)\",\"javax.servlet.http.HttpServlet.service(HttpServlet.java:660)\",\"org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)\",\"javax.servlet.http.HttpServlet.service(HttpServlet.java:741)\",\"org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)\",\"org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\",\"org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)\",\"org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\",\"org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:320)\",\"org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:126)\",\"org.springframework.security.web.access.intercept.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:90)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:118)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:137)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:111)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:158)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.authentication.www.BasicAuthenticationFilter.doFilterInternal(BasicAuthenticationFilter.java:204)\",\"org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter.doFilter(AbstractAuthenticationProcessingFilter.java:200)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:116)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:92)\",\"org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:77)\",\"org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:105)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:56)\",\"org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\",\"org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)\",\"org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:215)\",\"org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:178)\",\"org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:358)\",\"org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:271)\",\"org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\",\"org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\",\"org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\",\"org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\",\"org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\",\"org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\",\"org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\",\"org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\",\"org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\",\"org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\",\"org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\",\"org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:119)\",\"org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)\",\"org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)\",\"org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:202)\",\"org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)\",\"org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541)\",\"org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)\",\"org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)\",\"org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\",\"org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)\",\"org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:367)\",\"org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)\",\"org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:868)\",\"org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1639)\",\"org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)\",\"java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(Unknown Source)\",\"java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(Unknown Source)\",\"org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)\",\"java.base/java.lang.Thread.run(Unknown Source)\",\"Caused by: aero.minova.core.application.system.domain.ProcedureException: com.microsoft.sqlserver.jdbc.SQLServerException: ADO | 25 | msg.sql.51002 | Dieser Vorgang wurde bereits storniert\",\"aero.minova.core.application.system.controller.SqlProcedureController.processSqlProcedureRequest(SqlProcedureController.java:189)\",\"aero.minova.core.application.system.controller.SqlProcedureController.executeProcedure(SqlProcedureController.java:136)\",\"... 85 more\",\"Caused by: com.microsoft.sqlserver.jdbc.SQLServerException: ADO | 25 | msg.sql.51002 | Dieser Vorgang wurde bereits storniert\",\"com.microsoft.sqlserver.jdbc.SQLServerException.makeFromDatabaseError(SQLServerException.java:262)\",\"com.microsoft.sqlserver.jdbc.SQLServerStatement.getNextResult(SQLServerStatement.java:1624)\",\"com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement.doExecutePreparedStatement(SQLServerPreparedStatement.java:594)\",\"com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement$PrepStmtExecCmd.doExecute(SQLServerPreparedStatement.java:524)\",\"com.microsoft.sqlserver.jdbc.TDSCommand.execute(IOBuffer.java:7194)\",\"com.microsoft.sqlserver.jdbc.SQLServerConnection.executeCommand(SQLServerConnection.java:2979)\",\"com.microsoft.sqlserver.jdbc.SQLServerStatement.executeCommand(SQLServerStatement.java:248)\",\"com.microsoft.sqlserver.jdbc.SQLServerStatement.executeStatement(SQLServerStatement.java:223)\",\"com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement.execute(SQLServerPreparedStatement.java:505)\",\"aero.minova.core.application.system.controller.SqlProcedureController.calculateSqlProcedureResult(SqlProcedureController.java:259)\",\"aero.minova.core.application.system.controller.SqlProcedureController.processSqlProcedureRequest(SqlProcedureController.java:177)\",\"... 86 more\"]}},\"returnCodes\":[-1],\"returnCode\":-1}\n";
		SqlProcedureResult fromJson = gson.fromJson(body, SqlProcedureResult.class);

		SqlProcedureResult checkForError = dataService.checkForSQLError(fromJson);

		assertNotNull(checkForError);

	}
}
