using System.IO;
using System.Text;

namespace HabboWasm.Unity.Extensions
{
    internal static class BinaryWriterExtensions
    {
        public static void WriteStringNullTerminated(this BinaryWriter writer, string value)
        {
            writer.Write(Encoding.UTF8.GetBytes(value));
            writer.Write((byte) 0x00);
        }
    }
}