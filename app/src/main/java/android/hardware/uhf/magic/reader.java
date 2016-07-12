//package android.hardware.rfid;
package android.hardware.uhf.magic;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//import android.util.Log; //mifarereader
public class reader {
	static public Handler m_handler = null;
	static Boolean m_bASYC = false, m_bLoop = false, m_bOK = false;
	static byte[] m_buf = new byte[10240];
	static int m_nCount = 0, m_nReSend = 0, m_nread = 0;
	static int msound = 0;
	static int msound1 = 0;
	// 成功
	static SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
	// 失败
	static SoundPool mfailSoundPool = new SoundPool(1,
			AudioManager.STREAM_RING, 0);
	static public String m_strPCEPC = "";
	// 消息标示
	static public int msgreadepc = 1;// 读取标签EPC：
	static public int msgreadwrireepc = 2;// 读标签
	static public int msgreadwrite = 3;// 写标签
	static public int readover = 4;// 写标签
	static public int editepcsmsg = 5;
	static public int locklable = 6;
	static public int killlable = 7;

	/**
	 * 初始化设备
	 * 
	 * @param strpath
	 */
	static public native int init(String strpath);

	/**
	 * 打开设备
	 * 
	 * @param strpath
	 *            设备地址CM719是"//dev//ttyS4"
	 * @return 成功返回0，失败返回错误标志（-20产品不对，-1设备无法打开，1设备已打开， -2设备参数无法设置）
	 */
	static public native int Open(String strpath);

	/**
	 * 读数据
	 * 
	 * @param pout
	 *            存放读到得数据
	 * @param nStart
	 *            存放数据在pout的起始位置
	 * @param nCount
	 *            想读到得数据长度
	 * @return
	 */
	static public native int Read(byte[] pout, int nStart, int nCount);

	/**
	 * 关闭设备
	 */
	static public native void Close();

	/**
	 * 清楚设备缓存中数据
	 */
	static public native void Clean();

	/**
	 * 单次读取TID
	 * 
	 * @return 成功返回0x10,错误返回0x11
	 */
	static public native int Readtid(int tid_len);

	/**
	 * 单次轮询
	 * 
	 * @return 成功返回0x10,错误返回0x11
	 */
	static public native int Inventory();

	/**
	 * 多次轮询
	 * 
	 * @param ntimes
	 *            轮询次数，轮询次数限制为 0-65535次
	 * @return 成功返回0x10,错误返回0x11
	 */
	static public native int MultiInventory(int ntimes);

	/**
	 * 停止多次轮询
	 * 
	 * @return 成功返回0x10,错误返回0x11
	 */
	static public native int StopMultiInventory();

	/**
	 * 设置Select参数，并且设置在单次轮询或多次轮询 Inventory之前，先发送 Select指令。在多标签的情况下， 可以只对特定标签进行轮询
	 * Inventory操作。
	 * 
	 * @param selPa
	 *            参数(Target: 3b 000, Action: 3b 000, MemBank: 2b 01)
	 * @param nPTR
	 *            (以 bit为单位，非word)从 PC和 EPC存储位开始
	 * @param nMaskLen
	 *            Mask长度
	 * @param turncate
	 *            (0x00是Disable truncation，0x80是 Enable truncation)
	 * @param mask
	 * @return 成功返回0x00,错误返回非0
	 */
	static public native int Select(byte selPa, int nPTR, byte nMaskLen,
			byte turncate, byte[] mask);

	/**
	 * 设置发 送Select 指令
	 * 
	 * @param data
	 *            (0x01是取消 Select指令，0x00是发 Select 指令)
	 * @return 成功返回0x00,错误返回非0
	 */
	static public native int SetSelect(byte data);

	/**
	 * 读标签数据存储区
	 * 
	 * @param password
	 *            读密码，4个字节
	 * @param nUL
	 *            PC+EPC长度
	 * @param PCandEPC
	 *            PC+EPC数据
	 * @param membank
	 *            标签数据存储区
	 * @param nSA
	 *            读标签数据区地址偏移
	 * @param nDL
	 *            读标签数据区地址长度
	 * @return
	 */
	static public native int ReadLable(byte[] password, int nUL,
			byte[] PCandEPC, byte membank, int nSA, int nDL);

	/**
	 * 写标签数据存储区
	 * 
	 * @param password
	 *            密码4字节
	 * @param nUL
	 *            PC+EPC长度
	 * @param PCandEPC
	 *            PC+EPC数据
	 * @param membank
	 *            标签数据存储区
	 * @param nSA
	 *            写标签数据区地址偏移
	 * @param nDL
	 *            写标签数据区数据长度
	 * @param data
	 *            写入数据
	 * @return
	 */
	
	static public native int WriteLable(byte[] password, int nUL,
			byte[] PCandEPC, byte membank, int nSA, int nDL, byte[] data);

	/**
	 * 对单个标签，锁定 Lock 或者解锁 Unlock 该标签的数据存储区
	 * 
	 * @param password
	 *            锁定密码
	 * @param nUL
	 *            PC+EPC长度
	 * @param PCandEPC
	 *            PC+EPC数据
	 * @param nLD
	 *            锁定还是解锁命令
	 * @return
	 */

	static public native int Lock(byte[] password, int nUL, byte[] PCandEPC,
			int nLD);

	/**
	 * 灭活 Kill 标签
	 * 
	 * @param password
	 *            密码
	 * @param nUL
	 *            PC+EPC长度
	 * @param EPC
	 *            PC+EPC内容
	 * @return
	 */
	static public native int Kill(byte[] password, int nUL, byte[] EPC);

	/**
	 * 灭活标签 （结果通过Handle异步发送）
	 * 
	 * @param btReadId
	 *            读写器地址
	 * @param pbtAryPassWord
	 *            销毁密码（4个字节）
	 * @return
	 */
	static public int KillLables(byte[] password, int nUL, byte[] EPC) {
		Clean();
		int nret = Kill(password, nUL, EPC);
		if (!m_bASYC) {
			StartASYCKilllables();
		}
		return nret;
	}

	/**
	 * 获取参数
	 * 
	 * @return
	 */
	static public native int Query();

	/**
	 * 设置Query命令中的相关参数
	 * 
	 * @param nParam
	 *            参数为 2字节，有下面的具体参数按位拼接而成： DR(1 bit): DR=8(1b0), DR=64/3(1b1).
	 *            只支持 DR=8的模式 M(2 bit): M=1(2b00), M=2(2b01), M=4(2b10),
	 *            M=8(2b11). 只支持 M=1的模式 TRext(1 bit): No pilot tone(1b0), Use
	 *            pilot tone(1b1). 只支持 Use pilot tone(1b1)模式 Sel(2 bit):
	 *            ALL(2b00/2b01), ~SL(2b10), SL(2b11) Session(2 bit): S0(2b00),
	 *            S1(2b01), S2(2b10), S3(2b11) Target(1 bit): A(1b0), B(1b1) Q(4
	 *            bit): 4b0000-4b1111
	 * @return
	 */
	static public native int SetQuery(int nParam);

	/**
	 * 设置工作地区频段
	 * 
	 * @param region
	 *            Region Parameter 01 中国900MHz 04 中国800MHz 02 美国 03 欧洲 06 韩国
	 * @return
	 */
	static public native int SetFrequency(byte region);

	/**
	 * 设置工作信道
	 * 
	 * @param channel
	 *            中国900MHz 信道参数计算公式，Freq_CH 为信道频率： CH_Index =
	 *            (Freq_CH-920.125M)/0.25M
	 * 
	 *            中国800MHz 信道参数计算公式，Freq_CH 为信道频率： CH_Index =
	 *            (Freq_CH-840.125M)/0.25M
	 * 
	 *            美国信道参数计算公式，Freq_CH为信道频率： CH_Index = (Freq_CH-902.25M)/0.5M
	 * 
	 *            欧洲信道参数计算公式，Freq_CH为信道频率： CH_Index = (Freq_CH-865.1M)/0.2M
	 * 
	 *            韩国信道参数计算公式，Freq_CH为信道频率： CH_Index = (Freq_CH-917.1M)/0.2M
	 * @return
	 */
	static public native int SetChannel(byte channel);

	/**
	 * 获取工作信道
	 * 
	 * @return 中国900MHz 信道参数计算公式，Freq_CH 为信道频率： Freq_CH = CH_Index * 0.25M +
	 *         920.125M
	 * 
	 *         中国800MHz 信道参数计算公式，Freq_CH 为信道频率： Freq_CH = CH_Index * 0.25M +
	 *         840.125M
	 * 
	 *         美国信道参数计算公式，Freq_CH为信道频率： Freq_CH = CH_Index * 0.5M + 902.25M
	 * 
	 *         欧洲信道参数计算公式，Freq_CH为信道频率： Freq_CH = CH_Index * 0.2M + 865.1M
	 */
	static public native int GetChannel();

	/**
	 * 设置为自动跳频模式或者取消自动跳频模式
	 * 
	 * @param auto
	 *            0xFF 为设置自动跳频，0x00为取消自动跳频
	 * @return
	 */
	static public native int SetAutoFrequencyHopping(byte auto);

	/**
	 * 获取发射功率
	 * 
	 * @return 返回实际发射功率
	 */
	static public native int GetTransmissionPower();
	/**
	 * 设置发射功率
	 * @param nPower
	 * 发射功率
	 * @return
	 */
	static public native int SetTransmissionPower(int nPower);

	/**
	 * 设置发射连续载波或者关闭连续载波
	 * 
	 * @param bOn
	 *            0xFF 为打开连续波，0x00为关闭连续波
	 * @return
	 */
	static public native int SetContinuousCarrier(byte bOn);

	/**
	 * 获取当前读写器接收解调器参数
	 * 
	 * @param bufout
	 *            两个字节，第一个混频器增益，第二个中频放大器增益 混频器 Mixer增益表 Type Mixer_G(dB) 0x00 0
	 *            0x01 3 0x02 6 0x03 9 0x04 12 0x05 15 0x06 16 中频放大器 IF AMP增益表
	 *            Type IF_G(dB) 0x00 12 0x01 18 0x02 21 0x03 24 0x04 27 0x05 30
	 *            0x06 36 0x07 ,40
	 * @return
	 */
	static public native int GetParameter(byte[] bufout);

	/**
	 * 设置当前读写器接收解调器参数
	 * 
	 * @param bMixer
	 *            混频器增益
	 * @param bIF
	 *            中频放大器增益
	 * @param nThrd
	 *            信号调节阀值,信号解调阈值越小能解调的标签返回RSSI越低，但越不稳定，低于一
	 *            定值完全不能解调；相反阈值越大能解调的标签返回信号 RSSI越大，距离越近，越稳定。0x01B0是推荐的 最小值
	 * @return
	 */
	static public native int SetParameter(byte bMixer, byte bIF, int nThrd);

	/**
	 * 测试射频输入端阻塞信号
	 * 
	 * @param bufout
	 * @return
	 */
	static public native int ScanJammer(byte[] bufout);

	/**
	 * 测试射频输入端 RSSI信号大小，用于检测当前环境下有无读写器在工作
	 * @param bufout
	 * @return
	 */
	static public native int TestRssi(byte[] bufout);

	/**
	 * 设置IO端口的方向，读取 IO电平以及设置 IO电平
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 *            编号 描述 长度 说明 0 参数0 1 byte 操作类型选择: 0x00：设置 IO 方向； 0x01：设置 IO 电平；
	 *            0x02：读取 IO 电平。 要操作的管脚在参数1 中指定 1 参数1 1 byte 参数值范围为
	 *            0x01~0x04，分别对应要操作的端口 IO1~IO4 2 参数2 1 byte 参数值为0x00 或0x01。
	 *            Parameter0 Parameter2 描述 0x00 0x00 IO 配置为输入模式 0x00 0x01 IO
	 *            配置为输出模式 0x01 0x00 设置 IO 输出为低电平 0x01 0x01 设置 IO 输出为高电平 当参数0为
	 *            0x02 时，此参数无意义。
	 * @param bufout
	 * @return
	 */
	static public native int SetIOParameter(byte p1, byte p2, byte p3,
			byte[] bufout);

	/**
	 * @author Administrator_007 重启UHF模块： 2015.09
	 */
	public static void RestartUhf() {
		reader.Close();
		{
			android.hardware.uhf.magic.reader.init("/dev/ttyMT1");
			android.hardware.uhf.magic.reader.Open("/dev/ttyMT1");
		}
		// if (reader.SetTransmissionPower(1950) != 0x11) {
		// if (reader.SetTransmissionPower(1950) != 0x11) {
		// reader.SetTransmissionPower(1950);
		// }
		// }

	}

	/**
	 * 对比写入的EPC是否正确,如果相等则写入正确不相同则写入失败;
	 * 
	 * @param strwepc
	 *            新修改的EPC
	 * 
	 * **/
	static public int serachInventoryLables(String strwepc) {
		int nret = Inventory();
		if (!m_bASYC) {
			StartASYClablesbyoldepc(strwepc);
		}
		return nret;
	}

	/**
	 * 单次轮询
	 * 
	 * @return 成功返回0x10,错误返回0x11
	 */
	static public int ReadtidLables() {
		int nret = Readtid(12);
		if (!m_bASYC) {
			StartRead_TID();
		}
		return nret;
	}

	static public int ReadtidLablesLoop() {
		int nret = Readtid(12);
		m_bLoop = true;
		if (!m_bASYC) {
			StartRead_TID();
		}
		return nret;
	}

	static public int InventoryLables() {
		int nret = Inventory();
		if (!m_bASYC) {
			StartASYClables();
		}
		return nret;
	}

	static public int InventoryLablesLoop() {
		int nret = Inventory();
		m_bLoop = true;
		if (!m_bASYC) {
			StartASYClables();
		}
		return nret;
	}

	static public void StopLoop() {
		m_bLoop = false;
	}

	static public int MultInventoryLables() {
		int nret = MultiInventory(65535);
		if (!m_bASYC) {
			StartASYClables();
		}
		return nret;
	}

	static public int ReadLablesepc(byte[] password, int nUL, byte[] PCandEPC,
			byte membank, int nSA, int nDL, String newepc) {
		int nret = 0;
		if (!m_bASYC) {
			Clean();
			nret = ReadLable(password, nUL, PCandEPC, membank, nSA, nDL);
			m_bOK = false;
			m_nReSend = 0;
			StartASYCReadlablesEPC(newepc);
		}
		return nret;
	}

	/**
	 * 读标签（结果通过Handle异步发送，一张卡一条消息）
	 * 
	 * @param password
	 *            读密码，4个字节
	 * @param nUL
	 *            PC+EPC长度
	 * @param PCandEPC
	 *            PC+EPC数据
	 * @param membank
	 *            标签数据存储区
	 * @param nSA
	 *            读标签数据区地址偏移
	 * @param nDL
	 *            读标签数据区地址长度
	 * @return
	 */
	static public int ReadLables(byte[] password, int nUL, byte[] PCandEPC,
			byte membank, int nSA, int nDL) {
		int nret = 0;
		if (!m_bASYC) {
			Clean();
			nret = ReadLable(password, nUL, PCandEPC, membank, nSA, nDL);
			m_bOK = false;
			m_nReSend = 0;
			StartASYCReadlables();
		}
		return nret;
	}

	/**
	 * 对单个标签，锁定 Lock 或者解锁 Unlock 该标签的数据存储区
	 * 
	 * @param password
	 *            锁定密码
	 * @param nUL
	 *            PC+EPC长度
	 * @param PCandEPC
	 *            PC+EPC数据
	 * @param nLD
	 *            锁定还是解锁命令
	 * @return
	 */
	static public int LockLables(byte[] password, int nUL, byte[] PCandEPC,
			int nLD) {
		Clean();
		int nret = Lock(password, nUL, PCandEPC, nLD);
		if (!m_bASYC) {
			StartASYCLocklables();
		}
		return nret;
	}

	// static public int Selectlables()
	// /**
	// * 锁定标签 （结果通过Handle异步发送）
	// * @param btReadId 读写器地址
	// * @param pbtAryPassWord 访问密码（4个字节）
	// * @param btMembank 锁定区域（访问密码（4）、销毁密码（5）、EPC（3）、TID（2）、USER（1））
	// * @param btLockType 锁定类型（开放（0）、锁定（1）、永久开放（2）、永久锁定（3））
	// * @return
	// */
	// static public int LockLables(byte btReadId, byte[] pbtAryPassWord,
	// byte btMembank, byte btLockType) {
	// Clean();
	// int nret = LockTag(btReadId, pbtAryPassWord, btMembank, btLockType);
	// if (!m_bASYC) {
	// StartASYCLocklables();
	// }
	// return nret;
	// }

	/**
	 * 写标签（结果通过Handle异步发送，一张卡一条消息）
	 * 
	 * @param password
	 *            密码4字节
	 * @param nUL
	 *            PC+EPC长度
	 * @param PCandEPC
	 *            PC+EPC数据
	 * @param membank
	 *            标签数据存储区
	 * @param nSA
	 *            写标签数据区地址偏移
	 * @param nDL
	 *            写标签数据区数据长度
	 * @param data
	 *            写入数据
	 * @return
	 * New
	 */
	
	static public int Writelables(byte[] password, int nUL, byte[] PCandEPC,
			byte membank, int nSA, int nDL, byte[] data) {
		Clean();
		int nret = WriteLable(password, nUL, PCandEPC, membank, nSA, nDL, data);
		if (!m_bASYC) {
			m_bOK = false;
			m_nReSend = 0;
			StartASYCWritelables();
		}
		return nret;
	}

	static public int GetLockPayLoad(byte membank, byte Mask) {
		int nret = 0;
		switch (Mask) {
		case 0:
			switch (membank) {
			case 0:
				nret = 0x80000;
				break;
			case 1:
				nret = 0x80200;
				break;
			case 2:
				nret = 0xc0100;
				break;
			case 3:
				nret = 0xc0300;
				break;
			}
			break;
		case 1:
			switch (membank) {
			case 0:
				nret = 0x20000;
				break;
			case 1:
				nret = 0x20080;
				break;
			case 2:
				nret = 0x30040;
				break;
			case 3:
				nret = 0x300c0;
				break;
			}
			break;
		case 2:
			switch (membank) {
			case 0:
				nret = 0x8000;
				break;
			case 1:
				nret = 0x8020;
				break;
			case 2:
				nret = 0xc010;
				break;
			case 3:
				nret = 0xc030;
				break;
			}
			break;
		case 3:
			switch (membank) {
			case 0:
				nret = 0x2000;
				break;
			case 1:
				nret = 0x2008;
				break;
			case 2:
				nret = 0x3004;
				break;
			case 3:
				nret = 0x300c;
				break;
			}
			break;
		case 4:
			switch (membank) {
			case 0:
				nret = 0x0800;
				break;
			case 1:
				nret = 0x0802;
				break;
			case 2:
				nret = 0x0c01;
				break;
			case 3:
				nret = 0x0c03;
				break;
			}
			break;
		}
		return nret;
	}

	static void StartASYCKilllables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0;
				m_nCount = 0;
				m_nread = 0;
				while (m_handler != null) {
					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0) {
						m_nread++;
						if (m_nread > 5)
							break;

					}
					// Log.e("test",""+m_nCount+"="+m_nread);
					String str = reader.BytesToString(m_buf, 0, m_nCount);
					// Log.e("test",str);
					String[] substr = Pattern.compile("BB0165").split(str);
					// Log.e("test","sub="+substr.length);
					for (int i = 0; i < substr.length; i++) {
						if (substr[i].length() >= 10) {
							if (substr[i].substring(0, 10).equals("000100677E")) {
								Message msg = new Message();
								msg.what = killlable;
								msg.obj = "OK";
								m_handler.sendMessage(msg);
							} else {
								Message msg = new Message();
								msg.what = killlable;
								msg.obj = substr[i];
								m_handler.sendMessage(msg);
							}
						}

					}
				}
				m_bASYC = false;
			}
		});
		thread.start();
	}

	static void StartASYCLocklables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0;
				m_nCount = 0;
				m_nread = 0;
				while (m_handler != null) {
					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0) {
						m_nread++;
						if (m_nread > 5)
							break;
					}
					String str = reader.BytesToString(m_buf, 0, m_nCount);
					String[] substr = Pattern.compile("BB0182").split(str);
					for (int i = 0; i < substr.length; i++) {
						if (substr[i].length() >= 10) {
							if (substr[i].substring(0, 10).equals("000100847E")) {
								Message msg = new Message();
								msg.what = locklable;
								msg.obj = "OK";
								m_handler.sendMessage(msg);
							} else {
								Message msg = new Message();
								msg.what = locklable;
								msg.obj = substr[i];
								m_handler.sendMessage(msg);
							}
						}

					}
				}
				m_bASYC = false;
			}
		});
		thread.start();
	}

	static void StartASYCWritelables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0;
				m_nCount = 0;
				m_nread = 0;
				while (m_handler != null) {
					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0) {
						m_nread++;
						if (m_nread >10)
							break;
						// }
						// BB0139000C12121212122121321212121
						String str = reader.BytesToString(m_buf, 0, m_nCount);
						Log.e("testtesttest", str);// BB0149
						// String[] substr =
						// Pattern.compile("BB0149").split(str);
						// for (int i = 0; i < substr.length; i++)
						{
							if (str.length() >= 12) {
								int nlen = Integer.valueOf(str.substring(0, 4),
										16);

								if (str.substring(10, 12).equals("00")
										|| str.substring(10, 12).equals("B3")
										|| str.substring(10, 12).equals("B4")) {
									m_bOK = true;
									Message msg = new Message();
									msg.what = msgreadwrite;
									msg.obj = "OK";
									m_handler.sendMessage(msg);
									break;
								} else if (str.substring(10, 12).equals("10")) {
									// BB 01 FF 00 01 B3 B4 7E
									m_bOK = true;
									Message msg = new Message();
									msg.what = msgreadwrite;
									// msg.obj = "Error:" +
									// "The tag does not have the presence area or the specified EPC code is incorrect. ";
									msg.obj = "Error:该标签没有在场区或者指定的EPC 代码不对";
									m_handler.sendMessage(msg);
									break;
								} else if (str.substring(10, 12).equals("16")) {
									m_bOK = true;
									Message msg = new Message();
									msg.what = msgreadwrite;
									// msg.obj = "Error:"
									// +"Access Password Error";
									msg.obj = "Error:Access Password 不正确";
									m_handler.sendMessage(msg);
									break;
								} else if (str.substring(10, 12).equals("BF")) {
									m_bOK = true;
									Message msg = new Message();
									msg.what = msgreadwrite;
									msg.obj = "Error:标签不支持Error-code 返回";
									// msg.obj = "Error:"
									// +"Label does not support Error-code return";
									m_handler.sendMessage(msg);
									break;
								} else if (str.substring(10, 12).equals("B0")) {
									m_bOK = true;
									Message msg = new Message();
									msg.what = msgreadwrite;
									msg.obj = "Error:" + "未知错误";
									m_handler.sendMessage(msg);
									break;
								}

							}

						}

					}

					m_bASYC = false;
				}
			}
		});
		thread.start();
	}

	// static void StartASYCWritelables() {
	// m_bASYC = true;
	// Thread thread = new Thread(new Runnable() {
	// public void run() {
	// int nTemp = 0;
	// m_nCount = 0;
	// m_nread = 0;
	// while (m_handler != null) {
	// nTemp = Read(m_buf, m_nCount, 1024);
	// m_nCount += nTemp;
	// if (nTemp == 0) {
	// m_nread++;
	// if (m_nread > 10)
	// break;
	// // }
	// String str = reader.BytesToString(m_buf, 0, m_nCount);
	// Log.e("testtesttest", str);// BB0149
	// String[] substr = Pattern.compile("BB0149").split(str);
	// for (int i = 0; i < substr.length; i++) {
	// if (substr[i].length() >= 10) {
	// int nlen = Integer.valueOf(
	// substr[i].substring(0, 4), 16);
	// if (substr[i].equals("0001004B7E")
	// || substr[i].contains("0001B4B57E")
	// || substr[i].contains("001B3B47E")) {// (00 01B4B57E)//BB01FF001B3B47E
	// m_bOK = true;
	// Message msg = new Message();
	// msg.what = msgreadwrite;
	// msg.obj = "OK";
	// m_handler.sendMessage(msg);
	// break;
	// } else {
	// m_bOK = true;
	// Message msg = new Message();
	// msg.what = msgreadwrite;
	// msg.obj = "写入失败:" + substr[i];
	// m_handler.sendMessage(msg);
	// break;
	// }
	// }
	//
	// }
	//
	// }
	//
	// m_bASYC = false;
	// }
	// }
	// });
	// thread.start();
	// }

	// static void StartASYCReadlables() {
	// m_bASYC = true;
	// Thread thread = new Thread(new Runnable() {
	// public void run() {
	// boolean tag_find = false;
	// int nTemp = 0;
	// m_nCount = 0;
	// m_nread = 0;
	// while (m_handler != null) {
	//
	// nTemp = Read(m_buf, m_nCount, 1024);
	// m_nCount += nTemp;
	// if (nTemp == 0) {
	// // m_nread++;
	// // /if (m_nread > 5)
	// // break;
	// // }
	// String str = reader.BytesToString(m_buf, 0, m_nCount);
	// Log.e("1111111", str);
	// String[] substr = Pattern.compile("BB0139").split(str);
	// String tempstr = "";
	// for (int i = 0; i < substr.length; i++) {
	// if (substr[i].length() > 10) {
	// if (!substr[i].substring(0, 2).equals("BB")) {
	// m_bOK = true;
	// Message msg = new Message();
	// msg.what = reader.msgreadwrireepc;
	// msg.obj = substr[i].substring(4,
	// substr[i].length() - 4);
	// m_handler.sendMessage(msg);
	// DevBeep.PlayOK();
	// m_bASYC = false;
	// tag_find = true;
	// break;
	// } else {
	// Message msg = new Message();
	// msg.what = reader.msgreadwrireepc;
	// msg.obj = "读取失败";
	// m_handler.sendMessage(msg);
	// // mfailSoundPool.play(msound1, 1.0f, 1.0f,
	// // 0,
	// // 0, 1.0f);
	// DevBeep.PlayOK();
	// m_bASYC = false;
	// tag_find = true;
	//
	// }
	// }
	// }
	// }
	// if (tag_find) {
	// break;
	// } else {
	//
	// }
	//
	// }
	//
	// m_bASYC = false;
	// }
	// });
	// thread.start();
	// }
	static void StartASYCReadlables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				boolean tag_find = false;
				int nTemp = 0;
				m_nCount = 0;
				m_nread = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0) {
						// m_nread++;
						// /if (m_nread > 5)
						// break;
						// }
						String str = reader.BytesToString(m_buf, 0, m_nCount);
						Log.e("1111111", str);
						// BB0139000C 121212121212131313131313 24 7E
						// BB0139000C 121212121212131313131313 24 7E
						if (str.startsWith("BB0139")) {
							m_bOK = true;
							Message msg = new Message();
							msg.what = reader.msgreadwrireepc;
							msg.obj = str.substring(10, str.length() - 4);
							// msg.obj=convertHexToString(str.substring(10,
							// str.length() - 4));
							m_handler.sendMessage(msg);
							DevBeep.PlayOK();
							m_bASYC = false;
							tag_find = true;
							break;

						} else if (str.startsWith("BB01FF")) {
							Message msg = new Message();
							if (str.substring(10, 12).equals("09")) {
								// msg.obj =
								// "Error:The tag does not have the presence area or the specified EPC code is incorrect";
								msg.obj = "Error:该标签没有在场区或者指定的EPC 代码不对";
							} else if (str.substring(10, 12).equals("16")) {
								// msg.obj = "Error:Access Password Error";
								msg.obj = "果Access Password 不正确";
							} else if (str.substring(10, 12).equals("A4")) {
								msg.obj = "Error:指定的标签数据存储区被锁定并且/或者是永久锁定，而且锁定状态为不可写或不可读";
								// msg.obj =
								// "Error:The specified tag data storage area is locked and / or permanently Lock, and lock status is not to be written or not readable. ";
							} else if (str.substring(10, 12).equals("A3")) {
								// msg.obj =
								// "Error:The specified tag data storage area does not exist; or the label does not Supports the specified length of EPC, ";
								msg.obj = "Error:指定的标签数据存储区不存在；或者该标签不支持指定长度的EPC，比如XPC";
							} else if (str.substring(10, 12).equals("A0")) {
								// msg.obj = "unknown error ";
								msg.obj = "Error:未知错误";
							}
							msg.what = reader.msgreadwrireepc;
							m_handler.sendMessage(msg);
							DevBeep.PlayOK();
							m_bASYC = false;
							tag_find = true;
						}

					}
					if (tag_find) {
						break;
					} else {

					}

				}

				m_bASYC = false;
			}
		});
		thread.start();
	}

	static void StartASYCReadlablesEPC(final String newepc) {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				boolean tag_find = false;
				int nTemp = 0;
				m_nCount = 0;
				m_nread = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0) {
						// m_nread++;
						// if (m_nread > 5)
						// break;
						// }
						String str = reader.BytesToString(m_buf, 0, m_nCount);
						Log.e("1111111", str);
						String[] substr = Pattern.compile("BB0139").split(str);
						String tempstr = "";
						for (int i = 0; i < substr.length; i++) {
							if (substr[i].length() > 10) {
								// if (!substr[i].substring(0,2).equals("BB"))
								Log.e("wwwwwww",
										substr[i].substring(4,
												substr[i].length() - 4));
								Log.e("fffffff", newepc);
								if (substr[i].substring(4,
										substr[i].length() - 4).equals(newepc)) {
									m_bOK = true;
									Message msg = new Message();
									msg.what = reader.msgreadwrireepc;
									msg.obj = substr[i].substring(4,
											substr[i].length() - 4);
									m_handler.sendMessage(msg);
									DevBeep.PlayOK();
									m_bASYC = false;
									tag_find = true;
									break;
								} else {
									Message msg = new Message();
									msg.what = reader.msgreadwrireepc;
									msg.obj = "读取失败";
									m_handler.sendMessage(msg);
									// mfailSoundPool.play(msound1, 1.0f, 1.0f,
									// 0,
									// 0, 1.0f);
									DevBeep.PlayOK();
									m_bASYC = false;
									tag_find = true;

								}
							}
						}
					}
					if (tag_find) {
						break;
					} else {

					}

				}

				m_bASYC = false;
			}
		});
		thread.start();
	}

	static void StartRead_TID() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0, nIndex = 0;
				boolean tag_find = false;
				m_nCount = 0;
				m_nReSend = 0;
				nIndex = 0;
				while (m_handler != null) {
					// nIndex = m_nCount;
					nTemp = Read(m_buf, m_nCount, 10240 - m_nCount);
					m_nCount += nTemp;
					// Log.e("777777777777777777", "count=" + m_nCount);
					if (nTemp == 0) {
						// String str = reader.BytesToString(m_buf, nIndex,
						// m_nCount - nIndex);
						String str = reader.BytesToString(m_buf, 0, m_nCount);
						String[] substr = Pattern.compile("BB023A").split(str);
						// Log.e("9999999", "len=" + substr.length);
						for (int i = 0; i < substr.length; i++) {
							Log.e("777777", substr[i]);
							if (substr[i].length() > 16) {
								if (!substr[i].substring(0, 2).equals("BB")) {
									int nlen = Integer.valueOf(
											substr[i].substring(0, 4), 16);
									int epclen = Integer.valueOf(
											substr[i].substring(4, 6), 16);
									Log.e("EPClen", String.valueOf(epclen));
									if ((nlen > 3)
											&& (nlen < (substr[i].length() - 6) / 2)) {
										Message msg = new Message();
										msg.what = reader.msgreadepc;
										msg.obj = substr[i].substring(4,
												(nlen * 2 + 2));
										// 0009 E2 801105200056CD E7
										// 7EBB01FF000113147E
										Log.e("epc24", substr[i]);
										// msg.obj = substr[i].substring(6,
										// nlen * 2 + 4);
										// msg.obj = substr[i].substring(6,
										// epclen*2+6)
										// + ":"
										// + substr[i].substring(
										// (6+epclen*2),
										// (nlen*2+2));
										m_handler.sendMessage(msg);
										tag_find = true;
										m_bOK = true;
										DevBeep.PlayOK();
										break;
									}
								}
							}
						}
						if (tag_find && !m_bLoop) {
							break;
						}
						if (m_bLoop) {
							m_nCount = 0;
							ReadtidLablesLoop();
							tag_find = false;
						} else {
							if ((m_nReSend < 20) && (!tag_find)) {
								Readtid(12);
								m_nReSend++;
							} else {
								Log.e("CLOSE", "close device");
								/*
								 * reader.Close();
								 * android.hardware.uhf.magic.reader
								 * .init("/dev/ttyMT2");
								 * android.hardware.uhf.magic.reader
								 * .Open("/dev/ttyMT2"); if
								 * (reader.SetTransmissionPower(2100) != 0x11) {
								 * if (reader.SetTransmissionPower(2100) !=
								 * 0x11) { reader.SetTransmissionPower(2100); }
								 * }
								 */
								tag_find = false;
								break;

							}
						}

						if (m_nCount >= 1024)
							m_nCount = 0;
					}
				}
				// Log.e("end", "quit");
				m_bASYC = false;
			}
		});
		thread.start();
	}
	static void StartASYClables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0, nIndex = 0;
				boolean tag_find = false;
				m_nCount = 0;
				m_nReSend = 0;
				nIndex = 0;
				while (m_handler != null) {
					// nIndex = m_nCount;
					nTemp = Read(m_buf, m_nCount,10240-m_nCount);
					m_nCount += nTemp;
					if (nTemp == 0) {
						String str = reader.BytesToString(m_buf, nIndex,
								m_nCount - nIndex);
						Log.e("777777", str);
						String[] substr = Pattern.compile("BB0222").split(str);
						// Log.e("9999999", "len=" + substr.length);
						for (int i = 0; i < substr.length; i++) {
							Log.e("777777", substr[i]);
							if (substr[i].length() > 16) {
								if (!substr[i].substring(0, 2).equals("BB")) {
									Log.e("9999999888888", "len=" + substr[i]);
									int nlen = Integer.valueOf(
											substr[i].substring(0, 4), 16);
									if ((nlen > 3)
											&& (nlen < (substr[i].length() - 6) / 2)) {
										Message msg = new Message();
										msg.what = reader.msgreadepc;//(substr[i].length()
																		// - 12)
																		// / 2;
										msg.obj = substr[i].substring(6,
												(nlen * 2));
										m_handler.sendMessage(msg);
										tag_find = true;
										m_bOK = true;
										DevBeep.PlayOK();
									}
								}
							}
						}
						// if (tag_find) {
						// DevBeep.PlayOK();
						// }
						if (m_bLoop) {
							m_nCount = 0;
							InventoryLablesLoop();
						} else {

							if ((m_nReSend < 20) && (!tag_find)) {
								Inventory();
								m_nReSend++;
							} else {
								m_nCount = 0;
								Message msg = new Message();
								msg.what = reader.readover;
								msg.obj = "";
								m_handler.sendMessage(msg);
								Log.e("dqw---test", "break");
								break;
								// RestartUhf();
								// Inventory();
								// m_nReSend = 0;
							}
							//
						}
						if (m_nCount >= 1024)
							m_nCount = 0;
					}
				}
				// Log.e("end", "quit");
				m_bASYC = false;
			}
		});
		thread.start();
	}

	static {
		System.loadLibrary("uhf-tools");
		msound1 = mfailSoundPool.load(
				"/system/media/audio/notifications/Argon.ogg", 1);
		msound = mSoundPool.load(
				"/system/media/audio/notifications/Heaven.ogg", 1);
	}

	public static byte[] stringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String BytesToString(byte[] b, int nS, int ncount) {
		String ret = "";
		int nMax = ncount > (b.length - nS) ? b.length - nS : ncount;
		for (int i = 0; i < nMax; i++) {
			String hex = Integer.toHexString(b[i + nS] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static int byteToInt(byte[] b) // byteToInt
	{
		int t2 = 0, temp = 0;
		for (int i = 3; i >= 0; i--) {
			t2 = t2 << 8;
			temp = b[i];
			if (temp < 0) {
				temp += 256;
			}
			t2 = t2 + temp;

		}
		return t2;

	}

	public static int byteToInt(byte[] b, int nIndex, int ncount) // byteToInt
	{
		int t2 = 0, temp = 0;
		for (int i = 0; i < ncount; i++) {
			t2 = t2 << 8;
			temp = b[i + nIndex];
			if (temp < 0) {
				temp += 256;
			}
			t2 = t2 + temp;

		}
		return t2;

	}

	/**** int to byte ******/
	public static byte[] intToByte(int content, int offset) {

		byte result[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int j = offset; j < result.length; j += 4) {
			result[j + 3] = (byte) (content & 0xff);
			result[j + 2] = (byte) ((content >> 8) & 0xff);
			result[j + 1] = (byte) ((content >> 16) & 0xff);
			result[j] = (byte) ((content >> 24) & 0xff);
		}
		return result;
	}

	public static String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		// 49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);
			temp.append(decimal);
		}
		return sb.toString();
	}

	// 转化字符串为十六进制编码
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	// 转化十六进制编码为字符串
//	public static String toStringHex(String s) {
//		byte[] baKeyword = new byte[s.length() / 2];
//		for (int i = 0; i < baKeyword.length; i++) {
//			try {
//				baKeyword[i] = (byte) (0xff & Integer.parseInt(
//						s.substring(i * 2, i * 2 + 2), 16));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		try {
//			s = new String(baKeyword, "utf-8");// UTF-16le:Not
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return s;
//	}

	// 转化十六进制编码为字符串
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	
	//	System.out.println(encode("中文"));
		//System.out.println(decode(encode("中文")));
	

	/*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";
	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	/**
	 * 寻卡，查找修改的EPC是否成功：
	 * 
	 * @param strwepc
	 *            新修改的EPC
	 * @author Administrator
	 * **/
	static void StartASYClablesbyoldepc(final String strwepc) {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0, nIndex = 0;
				boolean tag_find = false;
				m_nCount = 0;
				m_nReSend = 0;
				nIndex = 0;
				while (m_handler != null) {
					// nIndex = m_nCount;
					nTemp = Read(m_buf, m_nCount, 10240 - m_nCount);
					m_nCount += nTemp;
					if (tag_find)
						break;
					if (nTemp == 0) {
						String str = reader.BytesToString(m_buf, nIndex,
								m_nCount - nIndex);
						String[] substr = Pattern.compile("BB0222").split(str);
						for (int i = 0; i < substr.length; i++) {
							if (substr[i].length() > 16) {
								if (!substr[i].substring(0, 2).equals("BB")) {
									int nlen = Integer.valueOf(
											substr[i].substring(0, 4), 16);
									if ((nlen > 3)
											&& (nlen < (substr[i].length() - 6) / 2)) {
										Message msg = new Message();
										msg.what = editepcsmsg;
										String newepc = substr[i].substring(10,
												10 + strwepc.length())
												.toString();
										Log.e("newepc:", newepc);
										Log.e("wepc:", strwepc);
										if (newepc.equals(strwepc)) {
											// if
											// (substr[i].substring(10,strwepc.length()).toString().equals(strwepc))
											// {
											msg.obj = "ok";
											m_handler.sendMessage(msg);
											tag_find = true;
											m_bOK = true;
											break;
										}
									}
								}
							}
						}// for循环结束
						if (tag_find) {
							mSoundPool.play(msound, 1.0f, 1.0f, 0, 0, 1.0f);
							break;
						}
						if ((m_nReSend < 20) && (!tag_find)) {
							Inventory();
							m_nReSend++;
							m_bASYC = false;
						} else if (!tag_find && m_nReSend >= 20) {
							Message msg1 = new Message();
							msg1.what = editepcsmsg;//
							msg1.obj = "error";
							m_handler.sendMessage(msg1);
							tag_find = false;
							break;
						}
						if (m_nCount >= 1024)
							m_nCount = 0;
					}
				}
				m_bASYC = false;
				// Log.e("end", "quit");
			}
		});
		thread.start();
	}

}
