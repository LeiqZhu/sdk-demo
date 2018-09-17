var ioc = {
	"default-netty" : {
		"netty.welcome" : "Hello Server for Netty",

		"netty.host" : "127.0.0.1",
		"netty.port" : 10118,

		// 0 none 1 common 2 pay 3 ad
		"netty.module" : 1,
		"netty.ssl" : 8443,

		"netty.ioBossNum" : 2,
		"netty.ioWorkerNum" : 20,

		"netty.ioThreadCoreNum" : 10,
		"netty.ioThreadMaxNum" : 20,
		"netty.maxWorkerInQueue" : 30,

		"netty.SO_BACKLOG" : 1024,
		"netty.SO_RCVBUF" : 32768,
		"netty.SO_SNDBUF" : 65536,
		"netty.SO_KEEPALIVE" : false,
		"netty.SO_REUSEADDR" : true,
		"netty.TCP_NODELAY" : true,
		"netty.CONNECT_TIMEOUT_MILLIS" : 3000,
		"netty.SO_TIMEOUT" : 8000,

		"netty.blockTimeout" : 10000,
		"netty.poolLogSpan" : 600000,

		// Request Handler 对象是否使用对象连接池
		"netty.useObjectPool" : true,

		// init_cache_span_time 初始化缓存的间隔时长
		"netty.initCacheSpanTime" : 1800000,

		"netty.is_send_to_firehose" : false,
		"netty.is_start_save_schedule" : true,
		"netty.is_start_pay_schedule" : true,
		// 是否使用本地缓存数据
		"netty.is_use_local_cache" : true
	}
}