
public class Order {
	public enum Type {
		MOVE,
		ATTACK,
		STOP;
	}
	public enum Status {
		PROCESSING,
		DONE,
		FAILED;
	}
	
	private Type type;
//	private boolean sequential;
//	private Status status;
//	private boolean done, failed;
//	private Status status;
	public static final StopOrder stopOrder = new StopOrder();
	
	Order (Type type) {
		this.type = type;
//		this.sequential = sequential;
//		this.status = Status.QUEUEING;
//		done = false;
//		failed = false;
//		status = Status.PROCESSING;
	}
	
	public Type getType() {
		return type;
	}

//	public boolean isSequential() {
//		return sequential;
//	}
	
//	public Status getStatus() {
//		return status;
//	}
	
//	public boolean isDone() {
////		return status == Status.DONE;
//		return status == Status.DONE;
//	}
	
//	public boolean isFailed() {
//		return status == Status.FAILED;
//	}
	
//	public void setProccesing() {
//		this.status = Status.PROCESSING;
//	}
	
//	public void setStatus(Status status) {
//		this.status = status;
//		this.done = true;
//	}
	
//	public void setFailed() {
////		this.status = Status.DONE;
//		this.failed = true;
//	}
}
