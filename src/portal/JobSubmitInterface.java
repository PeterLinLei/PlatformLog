// 
package portal;
import javax.servlet.http.HttpServletRequest;

public interface JobSubmitInterface {
	public void construct(HttpServletRequest request);	
	public String submitJob();
}

